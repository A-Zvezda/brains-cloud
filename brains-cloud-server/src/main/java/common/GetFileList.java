package common;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class GetFileList extends AbstractMessage  {
    private Set<String> fileList;

    public void setFileList(Set<String> fileList) {
        this.fileList = fileList;
    }

    public Set<String> getFileList() {
        return fileList;
    }

    public Set<String> getFileList(String dir) throws IOException {
        Set<String> fileList = new HashSet<>();
        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (!Files.isDirectory(file)) {
                    fileList.add(file.getFileName().toString());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }

}
