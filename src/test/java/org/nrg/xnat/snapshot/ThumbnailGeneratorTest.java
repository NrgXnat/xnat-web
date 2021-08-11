package org.nrg.xnat.snapshot;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nrg.xnat.snapshot.generator.impl.ThumbnailGenerator;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public class ThumbnailGeneratorTest {
    @Test
    @Disabled
    public void test() {
        // This test is useful for experimenting with the thumbnail generator but the test dicom was not put into the repo,
        // plus it requires a user to look at the resulting images and thus is not an automated test.
        try {
            File f = new File("/tmp/tn/in.dcm");

            Files.createDirectories(Paths.get("/tmp/tn"));

            ThumbnailGenerator generator     = new ThumbnailGenerator();
            BufferedImage      bufferedImage = generator.rescale(f, 0, 0.5f, 0.5f);
            File               dest          = new File("/tmp/tn/bicube.gif");
            generator.writeImage(dest, bufferedImage);

            generator.setAffineTransformOp(AffineTransformOp.TYPE_BILINEAR);
            bufferedImage = generator.rescale(f, 0, 0.5f, 0.5f);
            dest = new File("/tmp/tn/bilinear.gif");
            generator.writeImage(dest, bufferedImage);

            generator.setAffineTransformOp(AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            bufferedImage = generator.rescale(f, 0, 0.5f, 0.5f);
            dest = new File("/tmp/tn/nn.gif");
            generator.writeImage(dest, bufferedImage);
        } catch (IOException e) {
            fail("Unexpected exception", e);
        }
    }
}
