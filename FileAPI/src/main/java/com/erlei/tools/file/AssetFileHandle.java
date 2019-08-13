package com.erlei.tools.file;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Created by lll on 2019/8/6
 * Email : erleizh@gmail.com
 * Describe : 资产文件
 */
public class AssetFileHandle extends FileHandle {

    private AssetManager mAssetManager;
    private static final String sPrefix = "asset://";

    public AssetFileHandle(AssetManager assetManager, String path) {
        super(path);
        mAssetManager = assetManager;
    }

    public AssetFileHandle(AssetManager assetManager, File file) {
        super(file);
        mAssetManager = assetManager;
    }


    @NonNull
    @Override
    public OutputStream write() {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @NonNull
    @Override
    public OutputStream write(boolean append) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @NonNull
    @Override
    public OutputStream write(boolean append, int bufferSize) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @Override
    public void write(@Nullable InputStream input, boolean append) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @NonNull
    @Override
    public Writer writer(boolean append) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @NonNull
    @Override
    public Writer writer(boolean append, @Nullable String charset) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @Override
    public FileHandle writeBytes(byte[] bytes, boolean append) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @Override
    public FileHandle writeBytes(byte[] bytes, int offset, int length, boolean append) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @Override
    public FileHandle writeString(@NonNull String string, boolean append) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @Override
    public FileHandle writeString(@NonNull String string, boolean append, @Nullable String charset) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException("Cannot delete an asset file: " + file);
    }

    @Override
    public boolean deleteDirectory() {
        throw new UnsupportedOperationException("Cannot delete an asset file: " + file);
    }

    @Override
    public void emptyDirectory() {
        throw new UnsupportedOperationException("Cannot delete an asset file: " + file);
    }

    @Override
    public void emptyDirectory(boolean preserveTree) {
        throw new UnsupportedOperationException("Cannot delete an asset file: " + file);
    }

    @Override
    public FileHandle child(String name) {
        if (file.getPath().length() == 0) return new AssetFileHandle(mAssetManager, new File(name));
        return new AssetFileHandle(mAssetManager, new File(file, name));
    }

    @NonNull
    @Override
    public Writer writer() {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @Override
    public FileHandle writeString(@NonNull String string) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @Override
    public FileHandle writeLine(@NonNull String string) {
        throw new UnsupportedOperationException("Cannot write to an asset file:" + file);
    }

    @NonNull
    @Override
    public FileHandle parent() {
        File parent = file.getParentFile();
        if (parent == null) {
            parent = new File("/");
        }
        return new AssetFileHandle(mAssetManager, parent);
    }

    @NonNull
    @Override
    public FileHandle sibling(@NonNull String name) {
        if (file.getPath().length() == 0)
            throw new FileOperateException("Cannot get the sibling of the root.");
        return new AssetFileHandle(mAssetManager, new File(file.getParent(), name));
    }

    @Override
    public String[] listPath() {
        try {
            return mAssetManager.list(file.getPath());
        } catch (IOException e) {
            throw new FileOperateException("Error listing children: " + file, e);
        }
    }

    @Override
    public boolean isDirectory() {
        try {
            String[] list = mAssetManager.list(file.getPath());
            return list != null && list.length > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean exists() {
        try {
            mAssetManager.open(file.getPath()).close();
            return true;
        } catch (IOException e) {
            return isDirectory();
        }
    }

    @Nullable
    public AssetFileDescriptor getAssetFileDescriptor() {
        try {
            return mAssetManager.openFd(file.getPath());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public long length() {
        AssetFileDescriptor assetFileDescriptor = null;
        try {
            assetFileDescriptor = mAssetManager.openFd(file.getPath());
            return assetFileDescriptor.getLength();
        } catch (IOException ignored) {
            return 0;
        } finally {
            if (assetFileDescriptor != null) {
                try {
                    assetFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public FileHandle moveTo(FileHandle dest) {
        throw new UnsupportedOperationException("Cannot move an asset file: " + file);
    }

    @NonNull
    @Override
    public InputStream read() {
        try {
            return mAssetManager.open(file.getPath());
        } catch (IOException e) {
            throw new FileOperateException("Error reading file: " + file, e);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() + sPrefix.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return sPrefix + super.toString();
    }

    @Override
    public FileHandle mkdirs() {
        throw new UnsupportedOperationException("Cannot mkdirs with an asset file: " + file);
    }
}
