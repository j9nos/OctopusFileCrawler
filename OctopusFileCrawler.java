import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class OctopusFileCrawler {

    private final Path sourceDirectory;
    private final Path targetDirectory;
    private final LinkedList<String> keywords;
    private final HashMap<Path, Integer> fileHistory = new HashMap<>();

    public OctopusFileCrawler(final String sourceDirectory, final String targetDirectory, final String... keywords) {
        this.sourceDirectory = Path.of(sourceDirectory);
        this.targetDirectory = Path.of(targetDirectory);
        this.keywords = new LinkedList<>(List.of(keywords));
    }

    public void crawl() throws IOException {
        Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                try {
                    if (containsKeywords(file)) {
                        copyFile(file);
                    }
                } catch (final Exception e) {
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private boolean containsKeywords(final Path file) throws IOException {
        final LinkedList<String> copy = new LinkedList<>(keywords);
        try (final BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                for (int i = copy.size() - 1; i >= 0; i--) {
                    if (line.toLowerCase().contains(copy.get(i).toLowerCase())) {
                        copy.remove(i);
                    }
                }
                if (copy.size() == 0) {
                    return true;
                }
            }
            return copy.size() == 0;
        }
    }

    private void copyFile(final Path file) throws IOException {
        final Path fileName = file.getFileName();
        Integer fileCount = fileHistory.get(fileName);
        if (fileCount == null) {
            fileCount = 0;
        }
        fileHistory.put(fileName, fileCount += 1);
        final Path destinationPath = targetDirectory.resolve(fileCount + "_" + fileName);
        Files.copy(file, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println(file + " is copied to " + destinationPath);
    }

}
