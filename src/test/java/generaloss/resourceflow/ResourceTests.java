package generaloss.resourceflow;

import generaloss.rawlist.StringList;
import generaloss.resourceflow.resource.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipFile;

public class ResourceTests {

    @Test
    public void fileTest() throws IOException {
        // dir 0 -> 2
        final FileResource dir0 = Resource.file("testdir0");
        Assert.assertTrue(dir0.mkdir());
        Assert.assertTrue(dir0.exists());
        Assert.assertTrue(dir0.isDirectory());
        // file 0 -> 1
        final FileResource file0 = Resource.file("testfile0");
        Assert.assertTrue(file0.create());
        // exists
        Assert.assertTrue(file0.exists());
        // is file
        Assert.assertTrue(file0.isFile());
        // write
        file0.writeString("a\nb\nc");
        Assert.assertEquals("a\nb\nc", file0.readString());
        // rename
        final FileResource file1 = file0.rename("testfile1");
        Assert.assertEquals("testfile1", file1.name());
        // append
        file1.appendString("\nd");
        Assert.assertEquals("a\nb\nc\nd", file1.readString());
        // move
        final FileResource movedfile1 = file1.move("testdir0/" + file1.name());
        // append
        movedfile1.appendString("\ne");
        Assert.assertEquals("a\nb\nc\nd\ne", movedfile1.readString());
        movedfile1.delete();
        // file 2
        final FileResource file2 = movedfile1.parent().createChildFile("testfile2");
        Assert.assertTrue(file2.isFile());
        Assert.assertTrue(file2.exists());
        file2.delete();
        // dir 1
        final FileResource dir1 = movedfile1.parent().createChildDir("testdir1");
        Assert.assertTrue(dir1.isDirectory());
        Assert.assertTrue(dir1.exists());
        dir1.delete();
        final FileResource dir2 = dir0.move("testdir2", StandardCopyOption.REPLACE_EXISTING);
        Assert.assertEquals("testdir2", dir2.name());
        dir2.delete();
    }


    @Test
    public void fileTempFileTest1() throws IOException {
        final FileResource res = Resource.temp("jpize-temp-res-test-", ".txt");
        res.deleteOnExit();
        Assert.assertTrue(res.exists());
        Assert.assertTrue(res.isFile());
    }


    @Test
    public void internalDirTest1() {
        final InternalResource res = Resource.internal("/testdir");
        Assert.assertTrue(res.exists());
    }

    @Test
    public void internalFileTest1() {
        final InternalResource res = Resource.internal("/testfile1.txt");
        Assert.assertTrue(res.exists());
        Assert.assertArrayEquals(new String[] {"128", "256"}, res.readLines());
    }

    @Test
    public void internalFileTest2() {
        final InternalResource res = Resource.internal("/testdir/testfile2.txt");
        Assert.assertTrue(res.exists());
        Assert.assertArrayEquals(new String[] {"64", "16", "2"}, res.readLines());
    }

    @Test
    public void urlTest1() throws MalformedURLException {
        final URLResource res = Resource.url("https://repo1.maven.org/maven2/io/github/generaloss/resource-flow/25.8.1/resource-flow-25.8.1.pom");
        Assert.assertTrue(res.exists());
        Assert.assertEquals(84322616, res.readString().hashCode());
    }

    @Test
    public void zipTest1() throws IOException {
        final ZipFile file = new ZipFile("./src/test/resources/test.zip");
        final ZipResource[] resources = Resource.zip(file);
        Assert.assertEquals(4, resources.length);
        Assert.assertTrue(resources[0].isDirectory());
        Assert.assertTrue(resources[1].isFile());
        Assert.assertEquals("blocks", resources[0].name());
        Assert.assertEquals("dirt.json", resources[1].name());
    }

    @Test
    public void classpathTest1() {
        final ClasspathResource packageRes = Resource.classpath("generaloss/resourceflow/");
        final ClasspathResource libPackageRes = Resource.classpath("generaloss/rawlist/");

        Assert.assertTrue(packageRes.exists());
        Assert.assertTrue(libPackageRes.exists());
        Assert.assertTrue(packageRes.isDirectory());
        Assert.assertTrue(libPackageRes.isDirectory());
        Assert.assertTrue(new StringList(packageRes.listNames()).contains("ResUtils.class"));
        Assert.assertTrue(new StringList(libPackageRes.listNames()).contains("ArrayUtils.class"));

        final ClasspathResource classRes = Resource.classpath("generaloss/resourceflow/ResUtils.class");
        final ClasspathResource libClassRes = Resource.classpath("generaloss/rawlist/ArrayUtils.class");

        Assert.assertTrue(classRes.exists());
        Assert.assertTrue(libClassRes.exists());
        Assert.assertFalse(classRes.isDirectory());
        Assert.assertFalse(libClassRes.isDirectory());
        classRes.readString();
        libClassRes.readString();
    }

}
