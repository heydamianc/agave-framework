<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Pastebin</title>
		<style type="text/css">
			@import "${contextPath}/css/structure.css";
			@import "${contextPath}/css/scheme.css";
		</style>
		<script type="text/javascript" src="${contextPath}/js/prototype-1.6.0.2.js"></script>
		<script type="text/javascript" src="${contextPath}/js/pastebin.js"></script>
		</head>
		<body>
			<div id="main">
				<div id="top">
					<div class="pad">
						<h1>Pastebin</h1>
					</div>
				</div>
				<div id="contentwrap" class="equalize">
					<div id="content">
						<div class="pad">
							<p>
								This pastebin can help you collaborate with others by letting you paste a 
								snippet of code or text in a centralized location.  Every snippet is 
								assigned a unique URL that you can easily share with others.
							</p>
							<h2>Get Started!</h2>
							<p>
								Supply the following details to share a snippet of your own.
							</p>
							<form action="${contextPath}/create" id="createForm" method="post">
								<input type="hidden" name="snippetId" id="snippetId" />
								<table>
									<tbody>
										<tr>
											<td colspan="2">
												<textarea id="contents" name="contents" cols="80" rows="15">Enter snippet contents...</textarea>
											</td>
										</tr>
										<tr>
											<td><label for="owner">Your Name:</label></td>
											<td><input type="text" name="owner" size="40" /></td>
										</tr>
										<tr>
											<td>Language:</td>
											<td>
												<select name="syntaxLanguage">
													<option value="" />
													<option value="c++">C++</option>
													<option value="c#">C#</option>
													<option value="css">CSS</option>
													<option value="delphi">Delphi</option>
													<option value="html">HTML</option>
													<option value="java">Java</option>
													<option value="javascript">Javascript</option>
													<option value="php">PHP</option>
													<option value="python">Python</option>
													<option value="ruby">Ruby</option>
													<option value="sql">SQL</option>
													<option value="vb">Visual Basic</option>
													<option value="xml">XML</option>
												</select>
											</td>
										</tr>
										<tr>
											<td><label for="timeframe">Expires After:</label></td>
											<td>
												<select name="expiration">
													<option value="hour">An Hour</option>
													<option value="day">A Day</option>
													<option value="week">A Week</option>
													<option value="month">A Month</option>
													<option value="forever">Never</option>
												</select>
											</td>
										</tr>
										<tr>
											<td>Private:</td>
											<td><input type="checkbox" name="privateSnippet" />
										</tr>
										<tr>
											<td colspan="2">
												<input type="submit" value="Create Snippet" />
											</td>
										</tr>
									</tbody>
								</table>
							</form>
						</div>
					</div>
				</div>
				<div id="right" class="equalize">
					<div class="pad">
						<#include "./overview.ftl">
					</div>
				</div>
				<div id="foot">
					Copyright &copy; 2008 Damian Carrillo -
					<a href="http://agave-web-framework.googlecode.com/">Agave Web Framework</a>
				</div>
			</div>
		</div>
	</body>
</html>
