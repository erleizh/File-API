package com.erlei.tools.file;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.erlei.tools.file.FileAPI.LINE_SEPARATOR;

/**
 * Created by lll on 2019/8/2
 * Email : erleizh@gmail.com
 * Describe : 文件句柄，封装了对文件的常用操作
 */
public class FileHandle {

    protected File file;

    public FileHandle(@NonNull String fileName) {
        this.file = new File(fileName);
    }

    public FileHandle(@NonNull File file) {
        this.file = file;
    }


    /**
     * @return 获取文件的绝对路径
     */
    @NonNull
    public String path() {
        return file.getAbsolutePath();
    }

    @NonNull
    public File file() {
        return file;
    }

    /**
     * @return 文件的名称，不包含父路径。
     */
    @NonNull
    public String name() {
        return file.getName();
    }

    /**
     * @return 返回文件扩展名（不带点）或空字符串。
     */
    @NonNull
    public String extension() {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
    }

    /**
     * @return 返回文件或文件夹所处的文件夹
     */
    public String dir() {
        String name = file.getPath();
        int index = name.lastIndexOf(File.separator);
        if (index == -1) return File.separator;
        return name.substring(0, index + 1);
    }

    /**
     * @return 文件名，没有父路径或扩展名。
     */
    @NonNull
    public String nameWithoutExtension() {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return name;
        return name.substring(0, dotIndex);
    }


    /**
     * @return 没有扩展名的路径和文件名，例如 dir/dir2/file.png &gt; dir/dir2/file
     */
    @NonNull
    public String pathWithoutExtension() {
        String path = file.getPath();
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex == -1) return path;
        return path.substring(0, dotIndex);
    }

    /**
     * @return 当文件存在，并且是一个目录时 return true
     */
    public boolean isDirectory() {
        return file.isDirectory();
    }


    /**
     * @return 当文件存在，并且是一个文件时 return true
     */
    public boolean isFile() {
        return file.isFile();
    }


    /**
     * @return InputStream
     * @throws FileOperateException 如果文件句柄表示目录，不存在或无法读取。
     */
    @NonNull
    public InputStream read() {
        try {
            return new FileInputStream(file());
        } catch (FileNotFoundException e) {
            if (isDirectory())
                throw new FileOperateException("Cannot open a stream to a directory: " + file, e);
            throw new FileOperateException("Error reading file: " + file, e);
        }
    }

    /**
     * @param bufferSize 缓冲区大小
     * @return BufferedInputStream
     */
    @NonNull
    public BufferedInputStream read(int bufferSize) {
        return new BufferedInputStream(read(), bufferSize);
    }

    /**
     * @return Reader
     */
    @NonNull
    public Reader reader() {
        return new InputStreamReader(read());
    }

    /**
     * @param charset 字符集
     * @return Reader
     * @throws FileOperateException 如果文件句柄表示目录，不存在或无法读取。
     */
    @NonNull
    public Reader reader(@NonNull String charset) {
        InputStream stream = read();
        try {
            return new InputStreamReader(stream, charset);
        } catch (UnsupportedEncodingException ex) {
            StreamUtils.closeQuietly(stream);
            throw new FileOperateException("Error reading file: " + file, ex);
        }
    }

    /**
     * @param bufferSize 缓冲区大小
     * @return BufferedReader
     * @throws FileOperateException 如果文件句柄表示目录，不存在或无法读取。
     */
    @NonNull
    public BufferedReader reader(int bufferSize) {
        return new BufferedReader(new InputStreamReader(read()), bufferSize);
    }

    /**
     * @param bufferSize 缓冲区大小
     * @param charset    字符集
     * @return BufferedReader
     * @throws FileOperateException 如果文件句柄表示目录，不存在或无法读取。
     */
    @NonNull
    public BufferedReader reader(int bufferSize, @NonNull String charset) {
        InputStream stream = read();
        try {
            return new BufferedReader(new InputStreamReader(stream, charset), bufferSize);
        } catch (UnsupportedEncodingException ex) {
            StreamUtils.closeQuietly(stream);
            throw new FileOperateException("Error reading file: " + file, ex);
        }
    }

    /**
     * @return 使用平台的默认字符集将整个文件读入字符串。
     */
    public String readString() {
        return readString(null);
    }

    /**
     * @param charset 字符集
     * @return 使用指定字符集将整个文件读入字符串。
     * @throws FileOperateException 如果文件句柄表示目录，不存在或无法读取。
     */
    @NonNull
    public String readString(@Nullable String charset) {
        StringBuilder output = new StringBuilder(estimateLength());
        InputStreamReader reader = null;
        try {
            InputStream stream = read();
            if (charset == null)
                reader = new InputStreamReader(stream);
            else
                reader = new InputStreamReader(stream, charset);
            char[] buffer = new char[256];
            while (true) {
                int length = reader.read(buffer);
                if (length == -1) break;
                output.append(buffer, 0, length);
            }
        } catch (IOException ex) {
            throw new FileOperateException("Error reading file: " + file, ex);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
        return output.toString();
    }

    /**
     * 将整个文件读入字节数组。
     *
     * @return byte[]
     * @throws FileOperateException 如果文件句柄表示目录，不存在或无法读取。
     */
    @NonNull
    public byte[] readBytes() {
        InputStream input = read();
        try {
            return StreamUtils.copyStreamToByteArray(input, estimateLength());
        } catch (IOException ex) {
            throw new FileOperateException("Error reading file: " + file, ex);
        } finally {
            StreamUtils.closeQuietly(input);
        }
    }


    /**
     * 将整个文件读入字节数组。字节数组必须足够大以容纳文件的数据。
     *
     * @param bytes  要加载文件的数组
     * @param offset 开始写字节的偏移量
     * @param size   要读取的字节数, see {@link #length()}
     * @return 读取字节数
     * @throws FileOperateException 如果文件句柄表示目录，不存在或无法读取。
     */
    public int readBytes(byte[] bytes, int offset, int size) {
        InputStream input = read();
        int position = 0;
        try {
            while (true) {
                int count = input.read(bytes, offset + position, size - position);
                if (count <= 0) break;
                position += count;
            }
        } catch (IOException ex) {
            throw new FileOperateException("Error reading file: " + file, ex);
        } finally {
            StreamUtils.closeQuietly(input);
        }
        return position - offset;
    }

    /**
     * 返回用于写入此文件的流。如有必要，将创建父目录。
     * 文件如果存在则追加写入
     *
     * @return OutputStream
     * @throws FileOperateException 如果此文件句柄表示目录，或者无法写入
     */
    @NonNull
    public OutputStream write() {
        return write(true);
    }

    /**
     * 返回用于写入此文件的流。如有必要，将创建父目录。
     *
     * @param append 如果为false，则该文件将被覆盖（如果存在），否则将被追加。
     * @return OutputStream
     * @throws FileOperateException 如果此文件句柄表示目录，或者无法写入
     */
    @NonNull
    public OutputStream write(boolean append) {
        parent().mkdirs();
        try {
            return new FileOutputStream(file(), append);
        } catch (Exception ex) {
            if (isDirectory())
                throw new FileOperateException("Cannot open a stream to a directory: " + file, ex);
            throw new FileOperateException("Error writing file: " + file, ex);
        }
    }

    /**
     * 返回用于写入此文件的缓冲流。如有必要，将创建父目录。
     *
     * @param append     如果为false，则该文件将被覆盖（如果存在），否则将被追加。
     * @param bufferSize 缓冲区的大小。
     * @return OutputStream
     * @throws FileOperateException 如果此文件句柄表示目录，或者无法写入
     */
    @NonNull
    public OutputStream write(boolean append, int bufferSize) {
        return new BufferedOutputStream(write(append), bufferSize);
    }


    /**
     * 从指定的流中读取剩余的字节并将它们写入此文件。
     * 将自动关闭留。如有必要，将创建父目录
     *
     * @param input  输入流
     * @param append 如果为false，则该文件将被覆盖（如果存在），否则将被追加。
     * @throws FileOperateException 如果此文件句柄表示目录，或者无法写入
     */
    public void write(@Nullable InputStream input, boolean append) {
        if (input == null) return;
        OutputStream output = null;
        try {
            output = write(append);
            StreamUtils.copyStream(input, output);
        } catch (Exception ex) {
            throw new FileOperateException("Error stream writing to file: " + file, ex);
        } finally {
            StreamUtils.closeQuietly(input);
            StreamUtils.closeQuietly(output);
        }
    }

    /**
     * 返回使用默认字符集写入此文件的writer。如有必要，将创建父目录。
     * 文件如果存在则追加写入
     *
     * @return Writer
     * @throws FileOperateException 如果此文件句柄表示目录，或者无法写入
     */
    @NonNull
    public Writer writer() {
        return writer(true, null);
    }

    /**
     * 返回使用默认字符集写入此文件的writer。如有必要，将创建父目录。
     *
     * @param append 如果为false，则该文件将被覆盖（如果存在），否则将被追加。
     * @return Writer
     * @throws FileOperateException 如果此文件句柄表示目录，或者无法写入
     */
    @NonNull
    public Writer writer(boolean append) {
        return writer(append, null);
    }

    /**
     * 返回写入此文件的writer。如有必要，将创建父目录。
     *
     * @param append  如果为false，则该文件将被覆盖（如果存在），否则将被追加。
     * @param charset 可以为null以使用默认字符集。
     * @return Writer
     * @throws FileOperateException 如果此文件句柄表示目录，或者无法写入
     */
    @NonNull
    public Writer writer(boolean append, @Nullable String charset) {
        parent().mkdirs();
        try {
            FileOutputStream output = new FileOutputStream(file(), append);
            if (charset == null)
                return new OutputStreamWriter(output);
            else
                return new OutputStreamWriter(output, charset);
        } catch (Exception ex) {
            if (isDirectory())
                throw new FileOperateException("Cannot open a stream to a directory: " + file, ex);
            throw new FileOperateException("Error writing file: " + file, ex);
        }
    }

    /**
     * 将指定的字节写入文件。如有必要，将创建父目录。
     *
     * @param bytes  bytes
     * @param append 如果为false，则该文件将被覆盖（如果存在），否则将被追加。
     * @return FileHandle
     * @throws FileOperateException 如果此文件句柄表示目录，或者无法写入
     */
    public FileHandle writeBytes(byte[] bytes, boolean append) {
        OutputStream output = write(append);
        try {
            output.write(bytes);
        } catch (IOException ex) {
            throw new FileOperateException("Error writing file: " + file, ex);
        } finally {
            StreamUtils.closeQuietly(output);
        }
        return this;
    }

    /**
     * 将指定的字节写入文件。如有必要，将创建父目录。
     *
     * @param bytes  数据
     * @param length 要写入的字节数
     * @param offset 数据中的起始偏移量
     * @param append 如果为false，则该文件将被覆盖（如果存在），否则将被追加。
     * @return FileHandle
     * @throws FileOperateException 如果此文件句柄表示目录，或者无法写入
     */
    public FileHandle writeBytes(byte[] bytes, int offset, int length, boolean append) {
        OutputStream output = write(append);
        try {
            output.write(bytes, offset, length);
        } catch (IOException ex) {
            throw new FileOperateException("Error writing file: " + file, ex);
        } finally {
            StreamUtils.closeQuietly(output);
        }
        return this;
    }

    /**
     * 使用默认字符集将指定的字符串写入文件(追加写入)。如有必要，将创建父目录。
     *
     * @param string 要写入的字符串
     * @return FileHandle
     */
    public FileHandle writeString(@NonNull String string) {
        return writeString(string, true, null);
    }

    /**
     * 使用默认字符集将指定的字符串写入文件(追加写入)。并换行，如有必要，将创建父目录。
     *
     * @param string 要写入的字符串
     * @return FileHandle
     */
    public FileHandle writeLine(@NonNull String string) {
        assert LINE_SEPARATOR != null;
        return writeString(string, true, null).writeString(LINE_SEPARATOR);
    }

    /**
     * 使用默认字符集将指定的字符串写入文件。如有必要，将创建父目录。
     *
     * @param string 要写入的字符串
     * @param append 如果为false，则该文件将被覆盖（如果存在），否则将被追加。
     * @return FileHandle
     */
    public FileHandle writeString(@NonNull String string, boolean append) {
        return writeString(string, append, null);
    }


    /**
     * 使用指定的charset将指定的字符串写入文件。如有必要，将创建父目录。
     *
     * @param string  要写入的字符串
     * @param append  如果为false，则该文件将被覆盖（如果存在），否则将被追加。
     * @param charset 可以为null以使用默认字符集。
     * @return FileHandle
     */
    public FileHandle writeString(@NonNull String string, boolean append, @Nullable String charset) {
        Writer writer = null;
        try {
            writer = writer(append, charset);
            writer.write(string);
        } catch (Exception ex) {
            throw new FileOperateException("Error writing file: " + file, ex);
        } finally {
            StreamUtils.closeQuietly(writer);
        }
        return this;
    }

    /**
     * @return 返回此目录下所有的文件列表。如果此文件句柄表示文件而不是目录，则返回空列表
     */
    @NonNull
    public List<FileHandle> list() {
        String[] relativePaths = listPath();
        if (relativePaths == null || relativePaths.length == 0) return new ArrayList<>(0);
        List<FileHandle> handles = new ArrayList<>(relativePaths.length);
        for (int i = 0, n = relativePaths.length; i < n; i++)
            handles.add(i, child(relativePaths[i]));
        return handles;
    }

    public String[] listPath() {
        return file.list();
    }

    /**
     * @param filter the {@link FileFilter} to filter files
     * @return 返回此目录下所有满足指定过滤器的文件列表，如果此句柄表示文件而不是目录，则返回空列表。
     */
    @NonNull
    public List<FileHandle> list(FileFilter filter) {
        String[] relativePaths = listPath();
        if (relativePaths == null || relativePaths.length == 0) return new ArrayList<>(0);
        List<FileHandle> handles = new ArrayList<>(relativePaths.length);
        for (String path : relativePaths) {
            FileHandle child = child(path);
            if (!filter.accept(child.file())) continue;
            handles.add(child);
        }
        return handles;
    }

    /**
     * @param filter the {@link FilenameFilter} to filter files
     * @return 返回此目录下所有满足指定过滤器的文件列表，如果此句柄表示文件而不是目录，则返回空列表。
     */
    @NonNull
    public List<FileHandle> list(FilenameFilter filter) {
        String[] relativePaths = listPath();
        if (relativePaths == null || relativePaths.length == 0) return new ArrayList<>(0);
        List<FileHandle> handles = new ArrayList<>(relativePaths.length);
        for (String path : relativePaths) {
            if (!filter.accept(file, path)) continue;
            handles.add(child(path));
        }
        return handles;
    }

    /**
     * @param suffix 后缀
     * @return 返回此目录下指定后缀的文件列表，如果此句柄表示文件而不是目录，则返回空列表。
     */
    @NonNull
    public List<FileHandle> list(String suffix) {
        String[] relativePaths = listPath();
        if (relativePaths == null || relativePaths.length == 0) return new ArrayList<>(0);
        List<FileHandle> handles = new ArrayList<>(relativePaths.length);
        for (String path : relativePaths) {
            if (!path.endsWith(suffix)) continue;
            handles.add(child(path));
        }
        return handles;
    }


    /**
     * 返回具有指定名称的兄弟句柄
     *
     * @param name 文件名
     * @return FileHandle
     * @throws FileOperateException if this file is the root.
     */
    @NonNull
    public FileHandle sibling(@NonNull String name) {
        if (file.getPath().length() == 0)
            throw new FileOperateException("Cannot get the sibling of the root.");
        return new FileHandle(new File(file.getParent(), name));
    }

    @NonNull
    public FileHandle parent() {
        File parent = file.getParentFile();
        if (parent == null) {
            parent = new File("/");
        }
        return new FileHandle(parent);
    }

    private int estimateLength() {
        int length = (int) length();
        return length != 0 ? length : 512;
    }


    /**
     * 删除此文件或空目录并返回成功。不会删除包含子项的目录。
     *
     * @return <code> true </code>当且仅当文件或目录是
     * 已成功删除;否则，<code> false </code>
     */
    public boolean delete() {
        return file().delete();
    }

    /**
     * 以递归方式删除此文件或目录以及所有子项。
     *
     * @return <code> true </code>当且仅当文件或目录是
     * 已成功删除;否则，<code> false </code>
     */
    public boolean deleteDirectory() {
        return deleteDirectory(file());
    }


    /**
     * 以递归方式删除此目录的所有子项。
     */
    public void emptyDirectory() {
        emptyDirectory(false);
    }

    /**
     * 以递归方式删除此目录的所有子项。 （可选）保留文件夹结构。
     *
     * @param preserveTree 保留文件夹结构
     */
    public void emptyDirectory(boolean preserveTree) {
        emptyDirectory(file(), preserveTree);
    }

    /**
     * @param name 文件名
     * @return FileHandle
     */
    public FileHandle child(String name) {
        if (file.getPath().length() == 0) return new FileHandle(new File(name));
        return new FileHandle(new File(file, name));
    }

    /**
     * 将此文件或目录复制到指定的文件或目录
     * 如果
     *
     * @param dest 目标路径
     * @return dest FileHandle
     */
    public FileHandle copyTo(@NonNull FileHandle dest) {
        if (!isDirectory()) {
            if (dest.isDirectory()) dest = dest.child(name());
            copyFile(this, dest);
            return dest;
        }
        if (dest.exists()) {
            if (!dest.isDirectory())
                throw new FileOperateException("Destination exists but is not a directory: " + dest);
        } else {
            dest.mkdirs();
            if (!dest.isDirectory())
                throw new FileOperateException("Destination directory cannot be created: " + dest);
        }
        copyDirectory(this, dest.child(name()));
        return dest;
    }

    public boolean exists() {
        return file.exists();
    }

    /**
     * 将此文件移动到指定文件，如果文件已存在，则覆盖该文件。
     *
     * @param dest 目标路径
     * @return dest FileHandle
     */
    public FileHandle moveTo(FileHandle dest) {
        if (file().renameTo(dest.file())) return dest;
        copyTo(dest);
        delete();
        if (exists() && isDirectory()) deleteDirectory();
        return dest;
    }

    /**
     * @return 如果是文件，返回文件大小，如果是文件夹，返回文件夹的大小
     */
    public long length() {
        long len = 0;
        if (isDirectory()) {
            for (FileHandle fileHandle : list()) {
                len += fileHandle.length();
            }
        } else {
            return file().length();
        }
        return len;
    }

    /**
     * @return 返回格式化之后的文件大小
     */
    public String formattedSize() {
        return formatter(length());
    }

    public FileHandle mkdirs() {
        //noinspection ResultOfMethodCallIgnored
        file().mkdirs();
        return this;
    }

    public long lastModified() {
        return file().lastModified();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileHandle that = (FileHandle) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return file != null ? file.hashCode() : 0;
    }

    @NonNull
    public String toString() {
        return file.toString();
    }

    static public FileHandle tempFile(String prefix) {
        try {
            return new FileHandle(File.createTempFile(prefix, null));
        } catch (IOException ex) {
            throw new FileOperateException("Unable to create temp file.", ex);
        }
    }

    static public FileHandle tempDirectory(String prefix) {
        try {
            File file = File.createTempFile(prefix, null);
            if (!file.delete()) throw new IOException("Unable to delete temp file: " + file);
            if (!file.mkdir()) throw new IOException("Unable to create temp directory: " + file);
            return new FileHandle(file);
        } catch (IOException ex) {
            throw new FileOperateException("Unable to create temp file.", ex);
        }
    }

    static private void emptyDirectory(File file, boolean preserveTree) {
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (!file1.isDirectory()) {
                        //noinspection ResultOfMethodCallIgnored
                        file1.delete();
                    } else if (preserveTree)
                        emptyDirectory(file1, true);
                    else
                        deleteDirectory(file1);
                }
            }
        }
    }

    static private boolean deleteDirectory(File file) {
        emptyDirectory(file, false);
        return file.delete();
    }

    static private void copyFile(FileHandle source, FileHandle dest) {
        try {
            dest.write(source.read(), false);
        } catch (Exception ex) {
            throw new FileOperateException("Error copying source file: " + source.file + "\n" //
                    + "To destination: " + dest.file, ex);
        }
    }

    static private void copyDirectory(FileHandle sourceDir, FileHandle destDir) {
        destDir.mkdirs();
        List<FileHandle> files = sourceDir.list();
        for (FileHandle srcFile : files) {
            FileHandle destFile = destDir.child(srcFile.name());
            if (srcFile.isDirectory())
                copyDirectory(srcFile, destFile);
            else
                copyFile(srcFile, destFile);
        }
    }

    static String formatter(final long byteNum) {
        if (byteNum < 0) {
            return "0B";
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.2fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.2fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.2fMB", (double) byteNum / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.2fGB", (double) byteNum / 1073741824);
        }
    }
}
