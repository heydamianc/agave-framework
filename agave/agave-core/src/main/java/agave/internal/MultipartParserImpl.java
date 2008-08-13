/**
 * Copyright (c) 2008, Damian Carrillo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of 
 *     conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *     conditions and the following disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *   * Neither the name of the copyright holder's organization nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software without specific 
 *     prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package agave.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class MultipartParserImpl implements MultipartParser {

    private static enum MatchType {STREAM_END, PART_END, HEADERS_END};

    private static final String contentDisposition = "Content-Disposition:\\s*form-data;\\s*name=\"(.*)\"";

    private static final Pattern filePattern = Pattern.compile(contentDisposition + ";\\s*filename=\"(.+)\"");
    private static final Pattern filenamePattern = Pattern.compile("(.*)\\.(.*)");
    private static final Pattern parameterPattern = Pattern.compile(contentDisposition);
    private static final Pattern contentTypePattern = Pattern.compile("Content-Type:\\s*(.+)");
    private static final Pattern otherHeaderPattern = Pattern.compile("(\\S+):\\s*(.+?)");
    private static final Pattern blankPattern = Pattern.compile("\\s*");

    private Pattern delimiterPattern;
    private Pattern eosPattern;

    private Matcher delimiterMatcher;
    private Matcher eosMatcher;
    private Matcher fileMatcher;
    private Matcher parameterMatcher;
    private Matcher contentTypeMatcher;
    private Matcher otherHeaderMatcher;
    private Matcher blankMatcher;

    private Map<String, Collection<String>> parameters = new HashMap<String, Collection<String>>();
    private Map<String, Part> parts = new HashMap<String, Part>();

    public void parseInput(InputStream in) throws IOException {
        Scanner scanner = new Scanner(in);
        
        // patterns to match

        String delimiter = scanner.nextLine();
        delimiterPattern = Pattern.compile(delimiter);
        eosPattern = Pattern.compile(delimiter + "--");
      
        MatchType matchType = null;
        while (true) {
            Part part = new PartImpl();
            matchType = parseHeaders(scanner, part);

            if (matchType == MatchType.STREAM_END) {
               break; 
            } else if (matchType == MatchType.HEADERS_END) { 
                if (part.getFilename() == null) {
                    parseParameterValue(scanner, part);
                    if (!parameters.containsKey(part.getName())) {
                        parameters.put(part.getName(), new ArrayList<String>());
                    }
                    parameters.get(part.getName()).add(part.getParameterValue());
                } else {
                    parseFileContents(scanner, part);
                    parts.put(part.getName(), part);
                }
            }
        }

    }

    private MatchType parseHeaders(Scanner scanner, Part part) {
        if (!scanner.hasNextLine()) {
           return MatchType.STREAM_END;
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            delimiterMatcher = delimiterPattern.matcher(line);
            eosMatcher = eosPattern.matcher(line);
            fileMatcher = filePattern.matcher(line);
            parameterMatcher = parameterPattern.matcher(line);
            contentTypeMatcher = contentTypePattern.matcher(line);
            otherHeaderMatcher = otherHeaderPattern.matcher(line);
            blankMatcher = blankPattern.matcher(line);

            if (eosMatcher.matches()) {
                return MatchType.STREAM_END;
            } else if (line == null || "".equals(line) || blankMatcher.matches()) {
                return MatchType.HEADERS_END;
            } else if (fileMatcher.matches() && fileMatcher.groupCount() >= 2 
                && fileMatcher.group(1) != null && fileMatcher.group(2) != null) {
                part.setName(fileMatcher.group(1));
                part.setFilename(fileMatcher.group(2));  
            } else if (parameterMatcher.matches() && parameterMatcher.groupCount() >= 1 
                && parameterMatcher.group(1) != null) {
                part.setName(parameterMatcher.group(1));
            } else if (contentTypeMatcher.matches() && contentTypeMatcher.groupCount() >= 1
                && contentTypeMatcher.group(1) != null) {
                part.setContentType(contentTypeMatcher.group(1));
            } else if (otherHeaderMatcher.matches() && otherHeaderMatcher.groupCount() >= 2
                && otherHeaderMatcher.group(1) != null && otherHeaderMatcher.group(2) != null) {
                part.addHeader(otherHeaderMatcher.group(1), otherHeaderMatcher.group(2));
            }
        }
        throw new IllegalStateException("Unable to parse part headers");
    }

    private MatchType parseParameterValue(Scanner scanner, Part part) {
        MatchType matchType = null;
        StringBuilder parameterValue = new StringBuilder();      
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            delimiterMatcher = delimiterPattern.matcher(line);
            eosMatcher = eosPattern.matcher(line);

            if (eosMatcher.matches()) {
                matchType = MatchType.STREAM_END;
                break;
            } else if (delimiterMatcher.matches()) {
                matchType = MatchType.PART_END;
                break;
            } else {
                parameterValue.append(line);
            }
        }

        part.setParameterValue(parameterValue.toString());
        return matchType;
    }

    private MatchType parseFileContents(Scanner scanner, Part part) throws IOException {
        MatchType matchType = null;
        Matcher filenameMatcher = filenamePattern.matcher(part.getFilename());
        if (filenameMatcher.matches() && filenameMatcher.groupCount() >= 2) {
            File contents = File.createTempFile(filenameMatcher.group(1), filenameMatcher.group(2));
            if (contents.canWrite()) {
                FileWriter writer = new FileWriter(contents);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    delimiterMatcher = delimiterPattern.matcher(line);
                    eosMatcher = eosPattern.matcher(line);

                    if (eosMatcher.matches()) {
                        matchType = MatchType.STREAM_END;
                        break;
                    } else if (delimiterMatcher.matches()) {
                        matchType = MatchType.PART_END;
                        break;
                    } else {
                        writer.write(line);
                    }
                }
                writer.close();
            }
            part.setContents(contents);
        }

        return matchType;
    }
    
    public Map<String, Collection<String>> getParameters() {
        return parameters;
    }

    public Map<String, Part> getParts() {
        return parts;
    }
}
