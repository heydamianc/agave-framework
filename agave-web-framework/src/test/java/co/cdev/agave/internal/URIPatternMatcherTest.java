package co.cdev.agave.internal;

import org.junit.Assert;
import org.junit.Test;

import co.cdev.agave.URIPatternImpl;
import co.cdev.agave.URIPatternMatcherImpl;

public class URIPatternMatcherTest {

    @Test
    public void testMatches() throws Exception {
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/")).matches("/"));
        Assert.assertFalse(new URIPatternMatcherImpl(new URIPatternImpl("/")).matches("/one"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/*/")).matches("/one/two/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/*/")).matches("/one/two"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/*")).matches("/one/two/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/three/")).matches("/one/two/three/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/three/")).matches("/one/two/three"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/three")).matches("/one/two/three/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/three")).matches("/one/two/three"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/four")).matches("/one/two/three/four"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/four")).matches("/one/two/three/four/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/four/")).matches("/one/two/three/four"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/four/")).matches("/one/two/three/four/"));
        Assert.assertFalse(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/four/")).matches("/one/two/three"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/**/four/")).matches("/one/two/three/four/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/*/four/")).matches("/one/two/three/four/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/*/**/four/")).matches("/one/two/three/four/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/*/**/four/")).matches("/one/four/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**")).matches("/one/two/three/four/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/")).matches("/one/two/three/four/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/")).matches("/one/two/three/four"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**")).matches("/one/two/three/four"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**")).matches("/one/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**")).matches("/one"));
        Assert.assertFalse(new URIPatternMatcherImpl(new URIPatternImpl("/one/**")).matches("/on/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/**/four/*")).matches("/one/two/three/four/five"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/${var}")).matches("/one/two"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/${var}/")).matches("/one/two"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/${var}")).matches("/one/two/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/${var}/")).matches("/one/two/"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/one/two/")).matches("/ONE/tWo"));
        Assert.assertTrue(new URIPatternMatcherImpl(new URIPatternImpl("/One/Two")).matches("/one/TWO"));
        Assert.assertFalse(new URIPatternMatcherImpl(new URIPatternImpl("/one/${two}")).matches("/"));
        Assert.assertFalse(new URIPatternMatcherImpl(new URIPatternImpl("/one/")).matches("/one/two"));
        Assert.assertFalse(new URIPatternMatcherImpl(new URIPatternImpl("/one/two")).matches("/one"));
        Assert.assertFalse(new URIPatternMatcherImpl(new URIPatternImpl("/one/${var}")).matches("/one"));
    }
    
}
