package co.cdev.agave.web;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.cdev.agave.sample.ObjectGraph;
import co.cdev.agave.web.FormPopulator;
import co.cdev.agave.web.RequestParameterFormPopulator;

/**
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class FormPopulatorTest {

    Mockery context = new Mockery();
    HttpServletRequest request;

    @Before
    public void setup() throws Exception {
        request = context.mock(HttpServletRequest.class);
    }

    @Test
    public void testCollectParameters() throws Exception {
        Vector<String> parameterNames = new Vector<String>();
        parameterNames.add("a");
        parameterNames.add("b");
        parameterNames.add("two.a");
        parameterNames.add("two.b");
        parameterNames.add("three.a.b");
        parameterNames.add("three.a.c$see");
        parameterNames.add("three.a.c$glee");
        parameterNames.add("three.a.d!0");
        parameterNames.add("three.a.d!1");
        final Enumeration<String> enumeration = parameterNames.elements();

        context.checking(new Expectations() {{
            allowing(request).getParameterNames(); will(returnValue(enumeration));
            allowing(request).getParameterValues("a"); will(returnValue(new String[]{"m"}));
            allowing(request).getParameterValues("b"); will(returnValue(new String[]{"m", "n"}));
            allowing(request).getParameterValues("two.a"); will(returnValue(new String[]{"m"}));
            allowing(request).getParameterValues("two.b"); will(returnValue(new String[]{"m", "n"}));
            allowing(request).getParameterValues("three.a.b"); will(returnValue(new String[]{"m"}));
            allowing(request).getParameterValues("three.a.c$see"); will(returnValue(new String[]{"m"}));
            allowing(request).getParameterValues("three.a.c$glee"); will(returnValue(new String[]{"n"}));
            allowing(request).getParameterValues("three.a.d!0"); will(returnValue(new String[]{"m"}));
            allowing(request).getParameterValues("three.a.d!1"); will(returnValue(new String[]{"n"}));
            allowing(request).getLocale(); will(returnValue(Locale.ENGLISH));
            
        }});

        FormPopulator populator = new RequestParameterFormPopulator(request);

        Map<String, List<Object>> params = populator.getParams();

        Assert.assertNotNull(params);
        Assert.assertTrue(!params.isEmpty());

        Assert.assertEquals(1, params.get("a").size());
        Assert.assertEquals("m", params.get("a").get(0));

        Assert.assertEquals(2, params.get("b").size());
        Assert.assertEquals("m", params.get("b").get(0));
        Assert.assertEquals("n", params.get("b").get(1));

        Assert.assertEquals(1, params.get("two.a").size());
        Assert.assertEquals("m", params.get("two.a").get(0));

        Assert.assertEquals(2, params.get("two.b").size());
        Assert.assertEquals("m", params.get("two.b").get(0));
        Assert.assertEquals("n", params.get("two.b").get(1));

        Assert.assertEquals(1, params.get("three.a.b").size());
        Assert.assertEquals("m", params.get("three.a.b").get(0));

        Assert.assertEquals(1, params.get("three.a.c$see").size());
        Assert.assertEquals("m", params.get("three.a.c$see").get(0));

        Assert.assertEquals(1, params.get("three.a.c$glee").size());
        Assert.assertEquals("n", params.get("three.a.c$glee").get(0));

        Assert.assertEquals(1, params.get("three.a.d!0").size());
        Assert.assertEquals("m", params.get("three.a.d!0").get(0));

        Assert.assertEquals(1, params.get("three.a.d!1").size());
        Assert.assertEquals("n", params.get("three.a.d!1").get(0));
    }

    @Test
    public void testPopulate() throws Exception {
    
        Vector<String> parameterNames = new Vector<String>();
        
        parameterNames.add("cat");
        parameterNames.add("names");
        parameterNames.add("nickNames!0");
        parameterNames.add("nickNames!1");
        parameterNames.add("moodIndicators$teeth");
        parameterNames.add("moodIndicators$questionMarkTail");
        parameterNames.add("nested.cat");
        parameterNames.add("nested.age");
        parameterNames.add("nested.weight");
        parameterNames.add("nested.convertMe");
        parameterNames.add("nested.names");
        parameterNames.add("nested.nickNames!0");
        parameterNames.add("nested.nickNames!1");
        parameterNames.add("nested.moodIndicators$teeth");
        parameterNames.add("nested.moodIndicators$questionMarkTail");
        parameterNames.add("nested.bites$hard");
        parameterNames.add("nested.bites$soft");
        parameterNames.add("nested.bites$gnawing");
        parameterNames.add("numbers!0");
        parameterNames.add("numbers!1");
        parameterNames.add("nested.favoritePopStars$pop");
        parameterNames.add("nested.favoritePopStars$dance");

        final Enumeration<String> enumeration = parameterNames.elements();

        context.checking(new Expectations() {{
            allowing(request).getParameterNames(); will(returnValue(enumeration));
            allowing(request).getParameterValues("cat"); will(returnValue(new String[]{"tabby"}));
            allowing(request).getParameterValues("names"); will(returnValue(new String[]{"ookwensu", "monster"}));
            allowing(request).getParameterValues("nickNames!0"); will(returnValue(new String[]{"kitty"}));
            allowing(request).getParameterValues("nickNames!1"); will(returnValue(new String[]{"grub"}));
            allowing(request).getParameterValues("moodIndicators$teeth"); will(returnValue(new String[]{"mad"}));
            allowing(request).getParameterValues("moodIndicators$questionMarkTail"); will(returnValue(new String[]{"happy"}));
            allowing(request).getParameterValues("nested.cat"); will(returnValue(new String[]{"tabby"}));
            allowing(request).getParameterValues("nested.age"); will(returnValue(new String[]{"3"}));
            allowing(request).getParameterValues("nested.weight"); will(returnValue(new String[]{"12.2"}));
            allowing(request).getParameterValues("nested.convertMe"); will(returnValue(new String[]{"something"}));
            allowing(request).getParameterValues("nested.names"); will(returnValue(new String[]{"ookwensu", "monster"}));
            allowing(request).getParameterValues("nested.nickNames!0"); will(returnValue(new String[]{"kitty"}));
            allowing(request).getParameterValues("nested.nickNames!1"); will(returnValue(new String[]{"grub"}));
            allowing(request).getParameterValues("nested.moodIndicators$teeth"); will(returnValue(new String[]{"mad"}));
            allowing(request).getParameterValues("nested.moodIndicators$questionMarkTail"); will(returnValue(new String[]{"happy"}));
            allowing(request).getParameterValues("nested.bites$hard"); will(returnValue(new String[]{"2"}));
            allowing(request).getParameterValues("nested.bites$soft"); will(returnValue(new String[]{"6"}));
            allowing(request).getParameterValues("nested.bites$gnawing"); will(returnValue(new String[]{"1"}));
            allowing(request).getParameterValues("numbers!0"); will(returnValue(new String[]{"0"}));
            allowing(request).getParameterValues("numbers!1"); will(returnValue(new String[]{"1"}));
            allowing(request).getParameterValues("nested.favoritePopStars$pop"); will(returnValue(new String[]{"Prince"}));
            allowing(request).getParameterValues("nested.favoritePopStars$dance"); will(returnValue(new String[]{"Tatu"}));
            allowing(request).getLocale(); will(returnValue(Locale.ENGLISH));
        }});

        FormPopulator populator = new RequestParameterFormPopulator(request);
        ObjectGraph form = new ObjectGraph();
        populator.populate(form);

        Assert.assertEquals("tabby", form.getCat());
        Assert.assertTrue(form.getNames().contains("ookwensu"));
        Assert.assertTrue(form.getNames().contains("monster"));
        Assert.assertEquals("kitty", form.getNickNames().get(0));
        Assert.assertEquals("grub", form.getNickNames().get(1));
        Assert.assertEquals("mad", form.getMoodIndicators().get("teeth"));
        Assert.assertEquals("happy", form.getMoodIndicators().get("questionMarkTail"));
        Assert.assertEquals("tabby", form.getNested().getCat());
        Assert.assertEquals(3, (int) form.getNested().getAge());
        Assert.assertEquals(12.2, form.getNested().getWeight(), 0);
        Assert.assertEquals("Booyaka!", form.getNested().getConvertMe());
        Assert.assertTrue(form.getNested().getNames().contains("ookwensu"));
        Assert.assertTrue(form.getNested().getNames().contains("monster"));
        Assert.assertEquals("kitty", form.getNested().getNickNames().get(0));
        Assert.assertEquals("grub", form.getNested().getNickNames().get(1));
        Assert.assertEquals("mad", form.getNested().getMoodIndicators().get("teeth"));
        Assert.assertEquals("happy", form.getNested().getMoodIndicators().get("questionMarkTail"));
        Assert.assertEquals(2, (int) form.getNested().getBites().get("hard"));
        Assert.assertEquals(6, (int) form.getNested().getBites().get("soft"));
        Assert.assertEquals(1, (int) form.getNested().getBites().get("gnawing"));
        Assert.assertEquals(0, (int) form.getNumbers().get(0));
        Assert.assertEquals(1, (int) form.getNumbers().get(1));
        Assert.assertEquals("Prince", form.getNested().getFavoritePopStars().get("pop").name());
        Assert.assertEquals("Tatu", form.getNested().getFavoritePopStars().get("dance").name());
    }
}
