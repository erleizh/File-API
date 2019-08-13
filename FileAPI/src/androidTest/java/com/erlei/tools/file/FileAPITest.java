package com.erlei.tools.file;

import android.Manifest;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

/**
 * FileAPI test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FileAPITest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private static final String TAG = "FileAPITest";

    @Before
    public void before() {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        FileAPI.init(appContext,"EOM");

        FileAPI.internal("").deleteDirectory();
        FileAPI.external("").deleteDirectory();
        FileAPI.sdcard("").deleteDirectory();

        println("-------------init--------------");
    }


    @After
    public void after() {
    }

    @Test
    public void testAsset() {
        FileHandle file = FileAPI.assets("data.txt");
        boolean exists = file.exists();
        println(file + " exists " + exists);

        assertThrows(file::write, UnsupportedOperationException.class);
        assertThrows(file::writer, UnsupportedOperationException.class);
        assertThrows(file::delete, UnsupportedOperationException.class);
        assertThrows(file::deleteDirectory, UnsupportedOperationException.class);
        Assert.assertNotNull(file.read());
        Assert.assertNotNull(file.reader());
        Assert.assertNotNull(file.readBytes());
        Assert.assertEquals(file.readString(), "assets");

        Assert.assertFalse(file.isDirectory());
        long lastModified = file.lastModified();
        println(file + " lastModified " + lastModified);
        long length = file.length();
        println(file + " length " + length);
        String size = file.formattedSize();
        println(file + " size " + size);
        Assert.assertEquals(lastModified, 0);
        Assert.assertEquals(length, 6);
        Assert.assertEquals(size, "6.00B");

        Assert.assertEquals(file.path(), "/data.txt");
        Assert.assertEquals(file.name(), "data.txt");
        Assert.assertEquals(file.nameWithoutExtension(), "data");
        Assert.assertEquals(file.extension(), "txt");


        file = FileAPI.assets("shader");
        Assert.assertTrue(file.isDirectory());
        List<FileHandle> list = file.list();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(list.get(0).parent(), file);

        lastModified = file.lastModified();
        println(file + " lastModified " + lastModified);
        length = file.length();
        println(file + " length " + length);
        Assert.assertEquals(lastModified, 0);
        Assert.assertEquals(length, 0);

        assertThrows(file::readBytes, FileOperateException.class);
        assertThrows(file::readString, FileOperateException.class);

        FileAPI.assets("shader").copyTo(FileAPI.internal(FileAPI.CACHE));
        FileAPI.assets("shader/fragment.shader").copyTo(FileAPI.internal(FileAPI.CACHE));
        Assert.assertTrue(FileAPI.internal(FileAPI.CACHE).exists());
        Assert.assertTrue(FileAPI.internal(FileAPI.CACHE).isDirectory());
        Assert.assertTrue(FileAPI.internal(FileAPI.CACHE, "fragment.shader").exists());
        Assert.assertTrue(FileAPI.internal(FileAPI.CACHE, "fragment.shader").isFile());
        Assert.assertEquals(FileAPI.internal(FileAPI.CACHE, "fragment.shader").lastModified() / 1000, System.currentTimeMillis() / 1000);
    }


    @Test
    public void test() {
        FileHandle file = FileAPI.internal("data.txt");
        Assert.assertFalse(file.exists());
        String str = "0123456789";
        file.writeString(str);
        Assert.assertEquals(file.lastModified() / 1000, System.currentTimeMillis() / 1000);
        Assert.assertEquals(file.readString(), str);
        Assert.assertEquals(file.path(), FileAPI.internal() + "data.txt");

        Writer writer1 = file.writer();
        Assert.assertNotNull(writer1);
        OutputStream write = file.write();
        Assert.assertNotNull(write);
        StreamUtils.closeQuietly(write);
        InputStream read = file.read();
        Assert.assertNotNull(read);
        StreamUtils.closeQuietly(read);
        Reader reader = file.reader();
        Assert.assertNotNull(reader);
        StreamUtils.closeQuietly(reader);


        Writer writer = null;
        try {
            writer = file.writer(true, null);
            writer.write(str);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
        }
        Assert.assertEquals(file.readString(), str + str);

        FileHandle internal = FileAPI.internal(FileAPI.DATA);
        Assert.assertTrue(internal.mkdirs().isDirectory());
    }

    @Test
    public void testPath() {
        FileHandle internal = FileAPI.internal("1.txt");
        println(internal.path());
        Assert.assertEquals(internal.dir(), FileAPI.internal());
        FileHandle external = FileAPI.external("1.txt");
        println(external.path());
        Assert.assertEquals(external.dir(), FileAPI.external());
        FileHandle sdcard = FileAPI.sdcard("1.txt");
        println(sdcard.path());
        Assert.assertEquals(sdcard.dir(), FileAPI.sdcard());
        FileHandle absolute = FileAPI.absolute("/sdcard/1.txt");
        absolute.writeString("1234");
        println(absolute.path());
//        Assert.assertEquals(absolute.dir(), "/");
    }


    private void println(String log) {
        Log.d(TAG, log);
    }

    public final void assertThrows(Runnable r, Class<? extends Exception> e) {
        try {
            r.run();
        } catch (Exception ex) {
            if (!ex.getClass().equals(e)) {
                Assert.fail();
            }
            return;
        }
        Assert.fail();
    }
}
