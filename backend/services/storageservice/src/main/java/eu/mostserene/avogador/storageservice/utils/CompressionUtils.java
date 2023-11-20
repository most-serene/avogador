package eu.mostserene.avogador.storageservice.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class CompressionUtils {

    private CompressionUtils() {
    }

    public static File createTarGzipFolder(Path source) throws IOException {
        if (!Files.isDirectory(source)) {
            throw new IOException("Please provide a directory.");
        }

        String tarFile = source.getParent() + "/" + source.getFileName().toString() + ".tar.gz";
        try (OutputStream fOut = Files.newOutputStream(Path.of(tarFile));
             BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            Files.walkFileTree(source, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attributes) {

                    if (attributes.isSymbolicLink()) {
                        return FileVisitResult.CONTINUE;
                    }
                    Path targetFile = source.relativize(file);
                    try {
                        TarArchiveEntry tarEntry = new TarArchiveEntry(
                                file.toFile(), targetFile.toString());
                        tOut.putArchiveEntry(tarEntry);
                        Files.copy(file, tOut);
                        tOut.closeArchiveEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
            tOut.finish();
        }
        return new File(tarFile);
    }

}
