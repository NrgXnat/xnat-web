package org.nrg.xapi.model.dicomweb.dcm4che3;

import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.nio.file.Files;
        import java.text.MessageFormat;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.ResourceBundle;
        import java.util.concurrent.Executor;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

        import org.apache.commons.cli.CommandLine;
        import org.apache.commons.cli.OptionGroup;
        import org.apache.commons.cli.Options;
        import org.apache.commons.cli.Option;
        import org.apache.commons.cli.ParseException;
        import org.apache.commons.cli.PatternOptionBuilder;
        import org.dcm4che3.data.Tag;
        import org.dcm4che3.data.UID;
        import org.dcm4che3.data.Attributes;
        import org.dcm4che3.data.Fragments;
        import org.dcm4che3.data.VR;
        import org.dcm4che3.imageio.codec.Compressor;
        import org.dcm4che3.imageio.codec.Decompressor;
        import org.dcm4che3.imageio.codec.Transcoder;
        import org.dcm4che3.imageio.codec.TransferSyntaxType;
        import org.dcm4che3.io.DicomEncodingOptions;
        import org.dcm4che3.io.DicomInputStream;
        import org.dcm4che3.io.DicomOutputStream;
        import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
        import org.dcm4che3.tool.common.CLIUtils;
        import org.dcm4che3.util.Property;
        import org.dcm4che3.util.SafeClose;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 */
public class Dcm2Dcm2 {

    private static ResourceBundle rb =
            ResourceBundle.getBundle("org.dcm4che3.tool.dcm2dcm.messages");

    private String tsuid;
    private TransferSyntaxType tstype;
    private boolean retainfmi;
    private boolean nofmi;
    private DicomEncodingOptions encOpts = DicomEncodingOptions.DEFAULT;
    private final List<Property> params = new ArrayList<Property>();

    public final void setTransferSyntax(String uid) {
        this.tsuid = uid;
        this.tstype = TransferSyntaxType.forUID(uid);
        if (tstype == null) {
            throw new IllegalArgumentException(
                    "Unsupported Transfer Syntax: " + tsuid);
        }
    }

    public final void setRetainFileMetaInformation(boolean retainfmi) {
        this.retainfmi = retainfmi;
    }

    public final void setWithoutFileMetaInformation(boolean nofmi) {
        this.nofmi = nofmi;
    }

    public final void setEncodingOptions(DicomEncodingOptions encOpts) {
        this.encOpts = encOpts;
    }

    public void addCompressionParam(String name, Object value) {
        params.add(new Property(name, value));
    }

    private static Object toValue(String s) {
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            return s.equalsIgnoreCase("true") ? Boolean.TRUE :
                    s.equalsIgnoreCase("false") ? Boolean.FALSE
                            : s;
        }
    }

    @SuppressWarnings("static-access")
    private static CommandLine parseComandLine(String[] args)
            throws ParseException{
        Options opts = new Options();
        CLIUtils.addCommonOptions(opts);
        CLIUtils.addEncodingOptions(opts);
        OptionGroup tsGroup = new OptionGroup();
        tsGroup.addOption(Option.builder("t")
                .longOpt("transfer-syntax")
                .hasArg()
                .argName("uid")
                .desc(rb.getString("transfer-syntax"))
                .build());
        tsGroup.addOption(Option.builder()
                .longOpt("jpeg")
                .desc(rb.getString("jpeg"))
                .build());
        tsGroup.addOption(Option.builder()
                .longOpt("jpll")
                .desc(rb.getString("jpll"))
                .build());
        tsGroup.addOption(Option.builder()
                .longOpt("jlsl")
                .desc(rb.getString("jlsl"))
                .build());
        tsGroup.addOption(Option.builder()
                .longOpt("jlsn")
                .desc(rb.getString("jlsn"))
                .build());
        tsGroup.addOption(Option.builder()
                .longOpt("j2kr")
                .desc(rb.getString("j2kr"))
                .build());
        tsGroup.addOption(Option.builder()
                .longOpt("j2ki")
                .desc(rb.getString("j2ki"))
                .build());
        opts.addOptionGroup(tsGroup);
        OptionGroup fmiGroup = new OptionGroup();
        fmiGroup.addOption(Option.builder("F")
                .longOpt("no-fmi")
                .desc(rb.getString("no-fmi"))
                .build());
        fmiGroup.addOption(Option.builder("f")
                .longOpt("retain-fmi")
                .desc(rb.getString("retain-fmi"))
                .build());
        opts.addOptionGroup(fmiGroup);
        opts.addOption(Option.builder()
                .hasArg()
                .argName("N")
                .type(PatternOptionBuilder.NUMBER_VALUE)
                .desc(rb.getString("max-threads"))
                .longOpt("max-threads")
                .build());
        opts.addOption(Option.builder()
                .hasArg()
                .argName("max-error")
                .type(PatternOptionBuilder.NUMBER_VALUE)
                .desc(rb.getString("verify"))
                .longOpt("verify")
                .build());
        opts.addOption(Option.builder()
                .hasArg()
                .argName("size")
                .type(PatternOptionBuilder.NUMBER_VALUE)
                .desc(rb.getString("verify-block"))
                .longOpt("verify-block")
                .build());
        opts.addOption(Option.builder("q")
                .hasArg()
                .argName("quality")
                .type(PatternOptionBuilder.NUMBER_VALUE)
                .desc(rb.getString("quality"))
                .build());
        opts.addOption(Option.builder("Q")
                .hasArg()
                .argName("compression")
                .type(PatternOptionBuilder.NUMBER_VALUE)
                .desc(rb.getString("compression"))
                .build());
        opts.addOption(Option.builder("N")
                .hasArg()
                .argName("near-lossless")
                .type(PatternOptionBuilder.NUMBER_VALUE)
                .desc(rb.getString("near-lossless"))
                .build());
        opts.addOption(Option.builder("C")
                .hasArgs()
                .argName("name=value")
                .valueSeparator()
                .desc(rb.getString("compression-param"))
                .build());
        CommandLine cl = CLIUtils.parseComandLine(args, opts, rb, Dcm2Dcm.class);
        return cl;
    }

//    public static void main(String[] args) {
//        try {
//            CommandLine cl = parseComandLine(args);
//            Dcm2Dcm main = new Dcm2Dcm();
//            main.setEncodingOptions(CLIUtils.encodingOptionsOf(cl));
//            if (cl.hasOption("F")) {
//                if (transferSyntaxOf(cl, null) != null)
//                    throw new ParseException(rb.getString("transfer-syntax-no-fmi"));
//                main.setTransferSyntax(UID.ImplicitVRLittleEndian);
//                main.setWithoutFileMetaInformation(true);
//            } else {
//                main.setTransferSyntax(transferSyntaxOf(cl, UID.ExplicitVRLittleEndian));
//                main.setRetainFileMetaInformation(cl.hasOption("f"));
//            }
//            main.setLegacy(cl.hasOption("legacy"));
//
//            if (cl.hasOption("max-threads"))
//                main.setMaxThreads(((Number) cl.getParsedOptionValue("max-threads")).intValue());
//
//            if (cl.hasOption("verify"))
//                main.addCompressionParam("maxPixelValueError",
//                        cl.getParsedOptionValue("verify"));
//
//            if (cl.hasOption("verify-block"))
//                main.addCompressionParam("avgPixelValueBlockSize",
//                        cl.getParsedOptionValue("verify-block"));
//
//            if (cl.hasOption("q"))
//                main.addCompressionParam("compressionQuality",
//                        cl.getParsedOptionValue("q"));
//
//            if (cl.hasOption("Q"))
//                main.addCompressionParam("compressionRatiofactor",
//                        cl.getParsedOptionValue("Q"));
//
//            if (cl.hasOption("N"))
//                main.addCompressionParam("nearLossless",
//                        cl.getParsedOptionValue("N"));
//
//            String[] cparams = cl.getOptionValues("C");
//            if (cparams != null)
//                for (int i = 0; i < cparams.length;)
//                    main.addCompressionParam(cparams[i++], toValue(cparams[i++]));
//
//            @SuppressWarnings("unchecked")
//            final List<String> argList = cl.getArgList();
//            int argc = argList.size();
//            if (argc < 2)
//                throw new ParseException(rb.getString("missing"));
//            File dest = new File(argList.get(argc-1));
//            if ((argc > 2 || new File(argList.get(0)).isDirectory())
//                    && !dest.isDirectory())
//                throw new ParseException(
//                        MessageFormat.format(rb.getString("nodestdir"), dest));
//            main.mtranscode(argList.subList(0, argc - 1), dest);
//        } catch (ParseException e) {
//            System.err.println("dcm2dcm: " + e.getMessage());
//            System.err.println(rb.getString("try"));
//            System.exit(2);
//        } catch (Exception e) {
//            System.err.println("dcm2dcm: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(2);
//        }
//    }

    private static String transferSyntaxOf(CommandLine cl, String def) {
        return cl.hasOption("ivrle") ? UID.ImplicitVRLittleEndian
                : cl.hasOption("evrbe") ? UID.ExplicitVRBigEndianRetired
                : cl.hasOption("defl") ? UID.DeflatedExplicitVRLittleEndian
                : cl.hasOption("jpeg") ? UID.JPEGBaseline1
                : cl.hasOption("jpll") ? UID.JPEGLossless
                : cl.hasOption("jlsl") ? UID.JPEGLSLossless
                : cl.hasOption("jlsn") ? UID.JPEGLSLossyNearLossless
                : cl.hasOption("j2kr") ? UID.JPEG2000LosslessOnly
                : cl.hasOption("j2ki") ? UID.JPEG2000
                : cl.getOptionValue("t", def);
    }

    public void transcode(File src, final File dest) throws IOException {
        try (Transcoder transcoder = new Transcoder(src)) {
            transcoder.setIncludeFileMetaInformation(!nofmi);
            transcoder.setRetainFileMetaInformation(retainfmi);
            transcoder.setEncodingOptions(encOpts);
            transcoder.setDestinationTransferSyntax(tsuid);
            transcoder.setCompressParams(params.toArray(new Property[params.size()]));
            transcoder.transcode(new Transcoder.Handler(){
                @Override
                public OutputStream newOutputStream(Transcoder transcoder, Attributes dataset) throws IOException {
                    return new FileOutputStream(dest);
                }
            });
        } catch (Exception e) {
            Files.deleteIfExists(dest.toPath());
            throw e;
        }
    }

    private String adjustTransferSyntax(String tsuid, int bitsStored) {
        switch (tstype) {
            case JPEG_BASELINE:
                if (bitsStored > 8)
                    return UID.JPEGExtended24;
                break;
            case JPEG_EXTENDED:
                if (bitsStored <= 8)
                    return UID.JPEGBaseline1;
                break;
            default:
        }
        return tsuid;
    }

}