package ee.mas.fdpanel;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 import oshi.SystemInfo; import oshi.hardware.CentralProcessor; import oshi.hardware.ComputerSystem; import oshi.hardware.HardwareAbstractionLayer; import oshi.software.os.OperatingSystem; import oshi.util.Constants; import java.io.*; import java.nio.charset.Charset; import java.nio.charset.StandardCharsets; import java.nio.file.Files; import java.nio.file.Path; import java.security.MessageDigest; import java.security.NoSuchAlgorithmException; import java.util.ArrayList; import java.util.Arrays; import java.util.List; import java.util.Locale;
public class Verifile {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 public static final List<String> iun = Arrays.asList("03000200-0400-0500-0006-000700080009", "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF", "00000000-0000-0000-0000-000000000000"); private String mas_root/*()*/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ; public /*String*/ Verifile(String mas_root) { this.mas_root = mas_root; }                                                                                                                                                                                                                                                                                                                                                                                             public String MakeAttestation() throws NoSuchAlgorithmException, IOException {                                                  String vb = readLine("/sys/class/dmi/id/bios_version"); if (vb.isBlank()) { return "FAILED"; } File edition = new File(this.mas_root + "/edition.txt"); if (!edition.exists()) { return "FOREIGN"; } FileInputStream fstream = new FileInputStream(edition);/****************/BufferedReader br = new BufferedReader(new InputStreamReader(fstream)); StringBuilder textData = new StringBuilder(); String strLine = br.readLine(); int i = 0; int a = 0; int q = 1; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; while(strLine != null) {textData.append(strLine).append("\n"); a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; if (i == 10) { break; } a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; a = 1; q = 2; a = q; q = a; strLine = br.readLine(); i++;}        fstream.close(); MessageDigest digest = MessageDigest.getInstance("SHA-256"); byte[] he = digest.digest(textData.toString().getBytes(StandardCharsets.UTF_8)); for (int w = 0; w < 2; w++) { q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++;q++; } List<Byte> dv = new ArrayList<>(); for (byte b: "VF2".getBytes(StandardCharsets.US_ASCII)) {dv.add(b);}byte[] empty = new byte[0x4D - vb.getBytes(StandardCharsets.US_ASCII).length - he.length - g().getBytes().length];for (byte b: empty) { dv.add(b); }for (byte b: vb.getBytes(StandardCharsets.US_ASCII)) { dv.add(b); }for (byte b: he) { dv.add(b); }for (byte b: g().getBytes()) { dv.add(b); }File inputFile = new File(this.mas_root + "/verifile2.dat");if (inputFile.exists()) {byte[] hostData = Files.readAllBytes(inputFile.toPath());for (int k = 0; k < hostData.length; k++) {if (hostData[k] != dv.get(k)) {return "TA"+/*VERIFY*/"MPERED";}}return "VER" + "IFI" + "ED";} else {return "LE" + "GAC" + "Y";} /* not random data: )AU()=T#)GIA)A)S=GJHAS)=GJHASPOIGHJSAPIOG?=)#UG)=U#?=)GFJAPIOFHGÜ=SAJFPOASNMCOIBASH)=(IU=?#U?&)=%JÜRFOJAO?ISHJFG?=ASUJFGPIASHGFOPASGJASPIOGASPIOGHOPAHGFPOSHFIOASHFOI(ASHU)?UWT=)(AWYH?)FPWHNFIAHW)P?OHJAWOIGFIOPAWHVOIAUWHVIOPAWJFIOAWHFOIAHOSFHOIAFHIAOPHFOIUAWHEIFOAWBOIUCBWIPOHBCVOIUWABVIOWAHBOVUIWAIFHWOIFHAIOWFHIOASPHFIOSAHFIOASNBFOIASHBIOVPHBASOIVBASIOVHBSAOIFGHASIOFASIOFHBASOIFHSAOIFHASOIHIOCBASOIVIOPASHVIOASCJSAOIHVPSHJVOISAFPINASFIOSHBAVPOHSAVIPOASHVOIASBOICJSOICBASOIVBSIOAVBOAISBVOIASBGSJPVOSAOIVBSAOIVBOASIVBASOIVBASOIVBOISABVIOHCOIJSAIOBVIOASBFOAISUFDBASIOFBSAOIFBASIOFIOSAFBIOSABNIOSCIONOUIWBIYUEVFIUWGHUOIJDIOOIBCOIUAWBWCOCIBAWOICBAIOCBAWIOCBOIWABCOIWABCOIWABCOIWABOICBAIOCBWOICBOIWBCIOAWBCIOABWOICWBAIOABWCOIBCWOIACBAIOWNOCIBAWIOCBAOUWIBCOIAWBCOIAWBC*/ }    public /*String*/ void execute(String executable) throws IOException, NoSuchAlgorithmException {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                if (this.MakeAttestation().equals("VERIFIED") || this.MakeAttestation().equals("FOREIGN")) { Runtime r = Runtime.getRuntime(); r.exec(executable); } }  private String readLine(String fileName, Charset... charset) {Charset cs = charset.length > 0 ? charset[0] : StandardCharsets.UTF_8;FileInputStream fstream = null;try {fstream = new FileInputStream(fileName);} catch (FileNotFoundException e) {return "";}BufferedReader br = new BufferedReader(new InputStreamReader(fstream, cs));try {String strLine = br.readLine();fstream.close();return strLine;} catch (IOException e) {return "";}}
    public /*String*/ boolean cs(File file) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  try { if (this.MakeAttestation().equals("VERIFIED") || this.MakeAttestation().equals("FOREIGN")) { return Files.isSymbolicLink(file.toPath()); } } catch (NoSuchAlgorithmException | IOException e) { return false; } return false; } private String g() {SystemInfo systemInfo = new SystemInfo();OperatingSystem operatingSystem = systemInfo.getOperatingSystem();HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();ComputerSystem computerSystem = hardwareAbstractionLayer.getComputerSystem();String vendor = operatingSystem.getManufacturer();String processorSerialNumber = computerSystem.getSerialNumber();String uuid = computerSystem.getHardwareUUID();if (iun.contains(uuid.toUpperCase(Locale.ROOT))) {uuid = Constants.UNKNOWN;}String processorIdentifier = centralProcessor.getProcessorIdentifier().getIdentifier();int processors = centralProcessor.getLogicalProcessorCount();String delimiter = "-";return String.format(Locale.ROOT, "%08x", vendor.hashCode()) + delimiter+ String.format(Locale.ROOT, "%08x", processorSerialNumber.hashCode()) + delimiter+ String.format(Locale.ROOT, "%08x", uuid.hashCode()) + delimiter+ String.format(Locale.ROOT, "%08x", processorIdentifier.hashCode()) + delimiter + processors;}
           /* System.out.println("Hello, world!"); */
           /* return true; */}
/* } */