# File-API
封装了Android 的文件操作

#### FileAPI

```JAVA
/**
 * 初始化
 */
FileAPI#init(Context context, String baseDir)

/**
 * /data/user/0/[applicationId]/[BASE_DIR]/
 * <p>
 * 不需要读写权限，随着app卸载而删除
 * 私有文件夹，其他app无法访问
 *
 * @param path 相对路径
 * @return FileHandle
 */
FileAPI#internal(String path)

/**
 * /storage/emulated/0/Android/data/[applicationId]/[BASE_DIR]/
 * <p>
 * 不需要读写权限，随着app卸载而删除
 * 外部文件夹，其他app可以访问
 *
 * @param path 相对路径
 * @return FileHandle
 */
FileAPI#external(String path)

 /**
 * /storage/emulated/0/[BASE_DIR]/
 * <p>
 * 需要读写权限，app卸载不会删除，其他app可以访问
 *
 * @param path 相对路径
 * @return FileHandle
 */
FileAPI#sdcard(String path)

/**
 * 根路径 / 需要读写权限，app卸载不会删除，其他app可以访问
 *
 * @param path 文件绝对路径
 * @return FileHandle
 */    
FileAPI#absolute(String path)

/**
 * app 资产文件，只读
 *
 * @param path 文件相对路径
 * @return FileHandle
 */
FileAPI#assets(String path)
```



#### FileHandle

```java
FileHandle#FileHandle(java.lang.String)
FileHandle#FileHandle(java.io.File)
FileHandle#path()
FileHandle#file()
FileHandle#name()
FileHandle#extension()
FileHandle#dir()
FileHandle#nameWithoutExtension()
FileHandle#pathWithoutExtension()
FileHandle#isDirectory()
FileHandle#isFile()
FileHandle#read()
FileHandle#read(int)
FileHandle#reader()
FileHandle#reader(java.lang.String)
FileHandle#reader(int)
FileHandle#reader(int, java.lang.String)
FileHandle#readString()
FileHandle#readString(java.lang.String)
FileHandle#readBytes()
FileHandle#readBytes(byte[], int, int)
FileHandle#write()
FileHandle#write(boolean)
FileHandle#write(boolean, int)
FileHandle#write(java.io.InputStream, boolean)
FileHandle#writer()
FileHandle#writer(boolean)
FileHandle#writer(boolean, java.lang.String)
FileHandle#writeBytes(byte[], boolean)
FileHandle#writeBytes(byte[], int, int, boolean)
FileHandle#writeString(java.lang.String)
FileHandle#writeLine(java.lang.String)
FileHandle#writeString(java.lang.String, boolean)
FileHandle#writeString(java.lang.String, boolean, java.lang.String)
FileHandle#list()
FileHandle#list(java.io.FileFilter)
FileHandle#list(java.io.FilenameFilter)
FileHandle#list(java.lang.String)
FileHandle#sibling()
FileHandle#parent()
FileHandle#delete()
FileHandle#deleteDirectory()
FileHandle#emptyDirectory()
FileHandle#emptyDirectory(boolean)
FileHandle#child()
FileHandle#copyTo()
FileHandle#exists()
FileHandle#moveTo()
FileHandle#length()
FileHandle#formattedSize()
FileHandle#mkdirs()
FileHandle#lastModified()

```

