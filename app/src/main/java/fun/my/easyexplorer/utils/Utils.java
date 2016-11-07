package fun.my.easyexplorer.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fun.my.easyexplorer.model.AppInfo;

/**
 * Created by admin on 2016/9/10.
 */
public class Utils {

    public final static int SHORT = Toast.LENGTH_SHORT;
    public final static int LONG = Toast.LENGTH_LONG;
    private static final HashMap<String, String> MIME_MapTable = new HashMap<String, String>() {
        //{后缀名，MIME类型}
        {
            put("3gp", "video/3gpp");
            put("aab", "application/x-authoware-bin");
            put("aam", "application/x-authoware-map");
            put("aas", "application/x-authoware-seg");
            put("ai", "application/postscript");
            put("aif", "audio/x-aiff");
            put("aifc", "audio/x-aiff");
            put("aiff", "audio/x-aiff");
            put("als", "audio/X-Alpha5");
            put("amc", "application/x-mpeg");
            put("ani", "application/octet-stream");
            put("apk", "application/vnd.android.package-archive");
            put("asc", "text/plain");
            put("asd", "application/astound");
            put("asf", "video/x-ms-asf");
            put("asn", "application/astound");
            put("asp", "application/x-asap");
            put("asx", "video/x-ms-asf");
            put("au", "audio/basic");
            put("avb", "application/octet-stream");
            put("avi", "video/x-msvideo");
            put("awb", "audio/amr-wb");
            put("bcpio", "application/x-bcpio");
            put("bin", "application/octet-stream");
            put("bld", "application/bld");
            put("bld2", "application/bld2");
            put("bmp", "image/bmp");
            put("bpk", "application/octet-stream");
            put("bz2", "application/x-bzip2");
            put("c", "text/plain");
            put("cal", "image/x-cals");
            put("ccn", "application/x-cnc");
            put("cco", "application/x-cocoa");
            put("cdf", "application/x-netcdf");
            put("cgi", "magnus-internal/cgi");
            put("chat", "application/x-chat");
            put("class", "application/octet-stream");
            put("clp", "application/x-msclip");
            put("cmx", "application/x-cmx");
            put("co", "application/x-cult3d-object");
            put("cod", "image/cis-cod");
            put("conf", "text/plain");
            put("cpio", "application/x-cpio");
            put("cpp", "text/plain");
            put("cpt", "application/mac-compactpro");
            put("crd", "application/x-mscardfile");
            put("csh", "application/x-csh");
            put("csm", "chemical/x-csml");
            put("csml", "chemical/x-csml");
            put("css", "text/css");
            put("cur", "application/octet-stream");
            put("dcm", "x-lml/x-evm");
            put("dcr", "application/x-director");
            put("dcx", "image/x-dcx");
            put("dhtml", "text/html");
            put("dir", "application/x-director");
            put("dll", "application/octet-stream");
            put("dmg", "application/octet-stream");
            put("dms", "application/octet-stream");
            put("doc", "application/msword");
            put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            put("dot", "application/x-dot");
            put("dvi", "application/x-dvi");
            put("dwf", "drawing/x-dwf");
            put("dwg", "application/x-autocad");
            put("dxf", "application/x-autocad");
            put("dxr", "application/x-director");
            put("ebk", "application/x-expandedbook");
            put("emb", "chemical/x-embl-dl-nucleotide");
            put("embl", "chemical/x-embl-dl-nucleotide");
            put("eps", "application/postscript");
            put("eri", "image/x-eri");
            put("es", "audio/echospeech");
            put("esl", "audio/echospeech");
            put("etc", "application/x-earthtime");
            put("etx", "text/x-setext");
            put("evm", "x-lml/x-evm");
            put("evy", "application/x-envoy");
            put("exe", "application/octet-stream");
            put("fh4", "image/x-freehand");
            put("fh5", "image/x-freehand");
            put("fhc", "image/x-freehand");
            put("fif", "image/fif");
            put("fm", "application/x-maker");
            put("fpx", "image/x-fpx");
            put("fvi", "video/isivideo");
            put("gau", "chemical/x-gaussian-input");
            put("gca", "application/x-gca-compressed");
            put("gdb", "x-lml/x-gdb");
            put("gif", "image/gif");
            put("gps", "application/x-gps");
            put("gtar", "application/x-gtar");
            put("gz", "application/x-gzip");
            put("h", "text/plain");
            put("hdf", "application/x-hdf");
            put("hdm", "text/x-hdml");
            put("hdml", "text/x-hdml");
            put("hlp", "application/winhlp");
            put("hqx", "application/mac-binhex40");
            put("htm", "text/html");
            put("html", "text/html");
            put("hts", "text/html");
            put("ice", "x-conference/x-cooltalk");
            put("ico", "application/octet-stream");
            put("ief", "image/ief");
            put("ifm", "image/gif");
            put("ifs", "image/ifs");
            put("imy", "audio/melody");
            put("ins", "application/x-NET-Install");
            put("ips", "application/x-ipscript");
            put("ipx", "application/x-ipix");
            put("it", "audio/x-mod");
            put("itz", "audio/x-mod");
            put("ivr", "i-world/i-vrml");
            put("j2k", "image/j2k");
            put("jad", "text/vnd.sun.j2me.app-descriptor");
            put("jam", "application/x-jam");
            put("jar", "application/java-archive");
            put("java", "text/plain");
            put("jnlp", "application/x-java-jnlp-file");
            put("jpe", "image/jpeg");
            put("jpeg", "image/jpeg");
            put("jpg", "image/jpeg");
            put("jpz", "image/jpeg");
            put("js", "application/x-javascript");
            put("jwc", "application/jwc");
            put("kjx", "application/x-kjx");
            put("lak", "x-lml/x-lak");
            put("latex", "application/x-latex");
            put("lcc", "application/fastman");
            put("lcl", "application/x-digitalloca");
            put("lcr", "application/x-digitalloca");
            put("lgh", "application/lgh");
            put("lha", "application/octet-stream");
            put("lml", "x-lml/x-lml");
            put("lmlpack", "x-lml/x-lmlpack");
            put("log", "text/plain");
            put("lsf", "video/x-ms-asf");
            put("lsx", "video/x-ms-asf");
            put("lzh", "application/x-lzh");
            put("m13", "application/x-msmediaview");
            put("m14", "application/x-msmediaview");
            put("m15", "audio/x-mod");
            put("m3u", "audio/x-mpegurl");
            put("m3url", "audio/x-mpegurl");
            put("m4a", "audio/mp4a-latm");
            put("m4b", "audio/mp4a-latm");
            put("m4p", "audio/mp4a-latm");
            put("m4u", "video/vnd.mpegurl");
            put("m4v", "video/x-m4v");
            put("ma1", "audio/ma1");
            put("ma2", "audio/ma2");
            put("ma3", "audio/ma3");
            put("ma5", "audio/ma5");
            put("man", "application/x-troff-man");
            put("map", "magnus-internal/imagemap");
            put("mbd", "application/mbedlet");
            put("mct", "application/x-mascot");
            put("mdb", "application/x-msaccess");
            put("mdz", "audio/x-mod");
            put("me", "application/x-troff-me");
            put("mel", "text/x-vmel");
            put("mi", "application/x-mif");
            put("mid", "audio/midi");
            put("midi", "audio/midi");
            put("mif", "application/x-mif");
            put("mil", "image/x-cals");
            put("mio", "audio/x-mio");
            put("mmf", "application/x-skt-lbs");
            put("mng", "video/x-mng");
            put("mny", "application/x-msmoney");
            put("moc", "application/x-mocha");
            put("mocha", "application/x-mocha");
            put("mod", "audio/x-mod");
            put("mof", "application/x-yumekara");
            put("mol", "chemical/x-mdl-molfile");
            put("mop", "chemical/x-mopac-input");
            put("mov", "video/quicktime");
            put("movie", "video/x-sgi-movie");
            put("mp2", "audio/x-mpeg");
            put("mp3", "audio/x-mpeg");
            put("mp4", "video/mp4");
            put("mpc", "application/vnd.mpohun.certificate");
            put("mpe", "video/mpeg");
            put("mpeg", "video/mpeg");
            put("mpg", "video/mpeg");
            put("mpg4", "video/mp4");
            put("mpga", "audio/mpeg");
            put("mpn", "application/vnd.mophun.application");
            put("mpp", "application/vnd.ms-project");
            put("mps", "application/x-mapserver");
            put("mrl", "text/x-mrml");
            put("mrm", "application/x-mrm");
            put("ms", "application/x-troff-ms");
            put("msg", "application/vnd.ms-outlook");
            put("mts", "application/metastream");
            put("mtx", "application/metastream");
            put("mtz", "application/metastream");
            put("mzv", "application/metastream");
            put("nar", "application/zip");
            put("nbmp", "image/nbmp");
            put("nc", "application/x-netcdf");
            put("ndb", "x-lml/x-ndb");
            put("ndwn", "application/ndwn");
            put("nif", "application/x-nif");
            put("nmz", "application/x-scream");
            put("nokia-op-logo", "image/vnd.nok-oplogo-color");
            put("npx", "application/x-netfpx");
            put("nsnd", "audio/nsnd");
            put("nva", "application/x-neva1");
            put("oda", "application/oda");
            put("ogg", "audio/ogg");
            put("oom", "application/x-AtlasMate-Plugin");
            put("pac", "audio/x-pac");
            put("pae", "audio/x-epac");
            put("pan", "application/x-pan");
            put("pbm", "image/x-portable-bitmap");
            put("pcx", "image/x-pcx");
            put("pda", "image/x-pda");
            put("pdb", "chemical/x-pdb");
            put("pdf", "application/pdf");
            put("pfr", "application/font-tdpfr");
            put("pgm", "image/x-portable-graymap");
            put("pict", "image/x-pict");
            put("pm", "application/x-perl");
            put("pmd", "application/x-pmd");
            put("png", "image/png");
            put("pnm", "image/x-portable-anymap");
            put("pnz", "image/png");
            put("pot", "application/vnd.ms-powerpoint");
            put("ppm", "image/x-portable-pixmap");
            put("pps", "application/vnd.ms-powerpoint");
            put("ppt", "application/vnd.ms-powerpoint");
            put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            put("pqf", "application/x-cprplayer");
            put("pqi", "application/cprplayer");
            put("prc", "application/x-prc");
            put("prop", "text/plain");
            put("proxy", "application/x-ns-proxy-autoconfig");
            put("ps", "application/postscript");
            put("ptlk", "application/listenup");
            put("pub", "application/x-mspublisher");
            put("pvx", "video/x-pv-pvx");
            put("qcp", "audio/vnd.qcelp");
            put("qt", "video/quicktime");
            put("qti", "image/x-quicktime");
            put("qtif", "image/x-quicktime");
            put("r3t", "text/vnd.rn-realtext3d");
            put("ra", "audio/x-pn-realaudio");
            put("ram", "audio/x-pn-realaudio");
            put("rar", "application/x-rar-compressed");
            put("ras", "image/x-cmu-raster");
            put("rc", "text/plain");
            put("rdf", "application/rdf+xml");
            put("rf", "image/vnd.rn-realflash");
            put("rgb", "image/x-rgb");
            put("rlf", "application/x-richlink");
            put("rm", "audio/x-pn-realaudio");
            put("rmf", "audio/x-rmf");
            put("rmm", "audio/x-pn-realaudio");
            put("rmvb", "video/x-pn-realaudio");
            put("rnx", "application/vnd.rn-realplayer");
            put("roff", "application/x-troff");
            put("rp", "image/vnd.rn-realpix");
            put("rpm", "audio/x-pn-realaudio-plugin");
            put("rt", "text/vnd.rn-realtext");
            put("rte", "x-lml/x-gps");
            put("rtf", "application/rtf");
            put("rtg", "application/metastream");
            put("rtx", "text/richtext");
            put("rv", "video/vnd.rn-realvideo");
            put("rwc", "application/x-rogerwilco");
            put("s3m", "audio/x-mod");
            put("s3z", "audio/x-mod");
            put("sca", "application/x-supercard");
            put("scd", "application/x-msschedule");
            put("sdf", "application/e-score");
            put("sea", "application/x-stuffit");
            put("sgm", "text/x-sgml");
            put("sgml", "text/x-sgml");
            put("sh", "application/x-sh");
            put("shar", "application/x-shar");
            put("shtml", "magnus-internal/parsed-html");
            put("shw", "application/presentations");
            put("si6", "image/si6");
            put("si7", "image/vnd.stiwap.sis");
            put("si9", "image/vnd.lgtwap.sis");
            put("sis", "application/vnd.symbian.install");
            put("sit", "application/x-stuffit");
            put("skd", "application/x-Koan");
            put("skm", "application/x-Koan");
            put("skp", "application/x-Koan");
            put("skt", "application/x-Koan");
            put("slc", "application/x-salsa");
            put("smd", "audio/x-smd");
            put("smi", "application/smil");
            put("smil", "application/smil");
            put("smp", "application/studiom");
            put("smz", "audio/x-smd");
            put("snd", "audio/basic");
            put("spc", "text/x-speech");
            put("spl", "application/futuresplash");
            put("spr", "application/x-sprite");
            put("sprite", "application/x-sprite");
            put("spt", "application/x-spt");
            put("src", "application/x-wais-source");
            put("stk", "application/hyperstudio");
            put("stm", "audio/x-mod");
            put("sv4cpio", "application/x-sv4cpio");
            put("sv4crc", "application/x-sv4crc");
            put("svf", "image/vnd");
            put("svg", "image/svg-xml");
            put("svh", "image/svh");
            put("svr", "x-world/x-svr");
            put("swf", "application/x-shockwave-flash");
            put("swfl", "application/x-shockwave-flash");
            put("t", "application/x-troff");
            put("tad", "application/octet-stream");
            put("talk", "text/x-speech");
            put("tar", "application/x-tar");
            put("taz", "application/x-tar");
            put("tbp", "application/x-timbuktu");
            put("tbt", "application/x-timbuktu");
            put("tcl", "application/x-tcl");
            put("tex", "application/x-tex");
            put("texi", "application/x-texinfo");
            put("texinfo", "application/x-texinfo");
            put("tgz", "application/x-tar");
            put("thm", "application/vnd.eri.thm");
            put("tif", "image/tiff");
            put("tiff", "image/tiff");
            put("tki", "application/x-tkined");
            put("tkined", "application/x-tkined");
            put("toc", "application/toc");
            put("toy", "image/toy");
            put("tr", "application/x-troff");
            put("trk", "x-lml/x-gps");
            put("trm", "application/x-msterminal");
            put("tsi", "audio/tsplayer");
            put("tsp", "application/dsptype");
            put("tsv", "text/tab-separated-values");
            put("tsv", "text/tab-separated-values");
            put("ttf", "application/octet-stream");
            put("ttz", "application/t-time");
            put("txt", "text/plain");
            put("ult", "audio/x-mod");
            put("ustar", "application/x-ustar");
            put("uu", "application/x-uuencode");
            put("uue", "application/x-uuencode");
            put("vcd", "application/x-cdlink");
            put("vcf", "text/x-vcard");
            put("vdo", "video/vdo");
            put("vib", "audio/vib");
            put("viv", "video/vivo");
            put("vivo", "video/vivo");
            put("vmd", "application/vocaltec-media-desc");
            put("vmf", "application/vocaltec-media-file");
            put("vmi", "application/x-dreamcast-vms-info");
            put("vms", "application/x-dreamcast-vms");
            put("vox", "audio/voxware");
            put("vqe", "audio/x-twinvq-plugin");
            put("vqf", "audio/x-twinvq");
            put("vql", "audio/x-twinvq");
            put("vre", "x-world/x-vream");
            put("vrml", "x-world/x-vrml");
            put("vrt", "x-world/x-vrt");
            put("vrw", "x-world/x-vream");
            put("vts", "workbook/formulaone");
            put("wav", "audio/x-wav");
            put("wax", "audio/x-ms-wax");
            put("wbmp", "image/vnd.wap.wbmp");
            put("web", "application/vnd.xara");
            put("wi", "image/wavelet");
            put("wis", "application/x-InstallShield");
            put("wm", "video/x-ms-wm");
            put("wma", "audio/x-ms-wma");
            put("wmd", "application/x-ms-wmd");
            put("wmf", "application/x-msmetafile");
            put("wml", "text/vnd.wap.wml");
            put("wmlc", "application/vnd.wap.wmlc");
            put("wmls", "text/vnd.wap.wmlscript");
            put("wmlsc", "application/vnd.wap.wmlscriptc");
            put("wmlscript", "text/vnd.wap.wmlscript");
            put("wmv", "audio/x-ms-wmv");
            put("wmx", "video/x-ms-wmx");
            put("wmz", "application/x-ms-wmz");
            put("wpng", "image/x-up-wpng");
            put("wps", "application/vnd.ms-works");
            put("wpt", "x-lml/x-gps");
            put("wri", "application/x-mswrite");
            put("wrl", "x-world/x-vrml");
            put("wrz", "x-world/x-vrml");
            put("ws", "text/vnd.wap.wmlscript");
            put("wsc", "application/vnd.wap.wmlscriptc");
            put("wv", "video/wavelet");
            put("wvx", "video/x-ms-wvx");
            put("wxl", "application/x-wxl");
            put("x-gzip", "application/x-gzip");
            put("xar", "application/vnd.xara");
            put("xbm", "image/x-xbitmap");
            put("xdm", "application/x-xdma");
            put("xdma", "application/x-xdma");
            put("xdw", "application/vnd.fujixerox.docuworks");
            put("xht", "application/xhtml+xml");
            put("xhtm", "application/xhtml+xml");
            put("xhtml", "application/xhtml+xml");
            put("xla", "application/vnd.ms-excel");
            put("xlc", "application/vnd.ms-excel");
            put("xll", "application/x-excel");
            put("xlm", "application/vnd.ms-excel");
            put("xls", "application/vnd.ms-excel");
            put("xlt", "application/vnd.ms-excel");
            put("xlw", "application/vnd.ms-excel");
            put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            put("xm", "audio/x-mod");
            put("xml", "text/xml");
            put("xmz", "audio/x-mod");
            put("xpi", "application/x-xpinstall");
            put("xpm", "image/x-xpixmap");
            put("xsit", "text/xml");
            put("xsl", "text/xml");
            put("xul", "text/xul");
            put("xwd", "image/x-xwindowdump");
            put("xyz", "chemical/x-pdb");
            put("yz1", "application/x-yz1");
            put("z", "application/x-compress");
            put("zac", "application/x-zaurus-zac");
            put("zip", "application/zip");
            put("", "*/*");
        }
    };
    public static Map<String, String> type_map = new HashMap<String, String>() {
        {
            put("文本", "text/*");
            put("视频", "video/*");
            put("音频", "audio/*");
            put("图片", "image/*");
            put("应用", "application/vnd.android.package-archive");
            put("其他", "*/*");
        }
    };

    public static List<AppInfo> getAppInfoList5(Context context) {
        PackageManager pm = context.getPackageManager();
        List appInfos = new ArrayList();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int i = 0; i < 5; i++) {
            PackageInfo info = apps.get(i);
            String packageName = info.packageName;
            String appName = info.applicationInfo.loadLabel(pm).toString();
            Drawable drawable = info.applicationInfo.loadIcon(pm);
            AppInfo appInfo = new AppInfo(appName, packageName, drawable);
            appInfos.add(appInfo);
        }
        return appInfos;
    }

    public static List<AppInfo> getAppInfoList(Context context) {
        PackageManager pm = context.getPackageManager();
        List appInfos = new ArrayList();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo info : apps) {
            String packageName = info.packageName;
            String appName = info.applicationInfo.loadLabel(pm).toString();
            Drawable drawable = info.applicationInfo.loadIcon(pm);
            AppInfo appInfo = new AppInfo(appName, packageName, drawable);
            appInfos.add(appInfo);
        }
        return appInfos;
    }

    /**
     * 获取小数点后 n 位精度
     */
    public static double getNDegree(double num, int n) {
        BigDecimal bigDecimal = new BigDecimal(num);
        return bigDecimal.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double byteToGB(double size) {
        return size / 1024 / 1024 / 1024;
    }

    public static void messageShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }

    public static void messageLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        ;
    }

    public static void message(Context context, String msg, int duration) {
        Toast.makeText(context, msg, duration).show();
        ;
    }

    public static String getMimeType(File f) {
        String fileName = f.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String mimeType = MIME_MapTable.get(extension);

        return mimeType;
    }

    public static int getWindowWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getWindowHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static DisplayMetrics getWindowDisMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }
}
