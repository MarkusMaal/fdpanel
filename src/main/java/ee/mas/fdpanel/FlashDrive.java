package ee.mas.fdpanel;

import javafx.scene.paint.Color;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.DatatypeConverter;

public class FlashDrive {
    private String mount;
    private Boolean isValid;

    private String edition;

    private Boolean securePin;

    private String pin;

    private Color bg;
    private Color fg;

    private boolean isMas;
    public FlashDrive(String mount, boolean isMas) {
        this.mount = mount;
        this.isMas = isMas;
        this.initialize();
    }

    private void initialize() {
        File e_info = new File(mount + "/E_INFO/edition.txt");
        File upin = new File(mount + "/NTFS/config.sys");
        File spin = new File(mount + "/NTFS/spin.sys");
        this.isValid = e_info.exists() && !e_info.isDirectory() && ((upin.exists() && !upin.isDirectory()) || (spin.exists() && !spin.isDirectory()));
        this.edition = readLine(e_info.getAbsolutePath());
        if (!this.isMas) {
            this.bg = Color.rgb(255, 255, 255);
            this.fg = Color.rgb(0, 0, 0);
        } else {
            // load color scheme from home folder
            String home = System.getProperty("user.home");
            String theme_setting = this.readLine(home + "/.mas/scheme.cfg");
            String[] bg_str = theme_setting.split(";")[0].split(":");
            String[] fg_str = theme_setting.split(";")[1].split(":");
            this.bg = Color.rgb(Integer.parseInt(bg_str[0]), Integer.parseInt(bg_str[1]), Integer.parseInt(bg_str[2]));
            this.fg = Color.rgb(Integer.parseInt(fg_str[0]), Integer.parseInt(fg_str[1]), Integer.parseInt(fg_str[2]));
        }
        this.ReloadPin();
    }

    public boolean IsSecurePin() {
        return this.securePin;
    }

    private void ReloadPin() {
        File upin = new File(mount + "/NTFS/config.sys");
        File spin = new File(mount + "/NTFS/spin.sys");
        if (upin.exists() && !upin.isDirectory()) {
            String uPin = readLine(upin.getAbsolutePath());
            this.securePin = uPin.contains("Ebaturvaline PIN kood keelatud");
            if (securePin) {
                this.pin = readLine(spin.getAbsolutePath());
            } else {
                this.pin = readLine(upin.getAbsolutePath());
            }
        } else {
            this.pin = readLine(spin.getAbsolutePath());
            this.securePin = true;
        }
    }

    private String readLine(String fileName, Charset... charset) {
        Charset cs = charset.length > 0 ? charset[0] : StandardCharsets.UTF_8;
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            return "";
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream, cs));

        try {
            String strLine = br.readLine();
            //Close the input stream
            fstream.close();
            return strLine;
        } catch (IOException e) {
            return "";
        }
    }

    public String GetDiskSize() throws IOException {
        String newStr = "";
        FileStore store = Files.getFileStore(Path.of(this.mount));
        long capacity = store.getTotalSpace();
        float capacityGiga = (float) capacity / 1000f / 1000f / 1000f;
        float capacityGibi = (float) capacity / 1024f / 1024f / 1024f;
        DecimalFormat df = new DecimalFormat("###.##");
        newStr = df.format(capacityGiga) + " GB (" + df.format(capacityGibi) + " GiB)";
        return newStr;
    }

    public Long GetOccupiedSpace() throws IOException {
        FileStore store = Files.getFileStore(Path.of(this.mount));
        return store.getTotalSpace() - store.getUsableSpace();
    }

    public Long GetFreeSpace() throws IOException {
        FileStore store = Files.getFileStore(Path.of(this.mount));
        return store.getUsableSpace();
    }

    public String GetEdition() {
        return this.edition;
    }

    public String GetMount() {
        return this.mount;
    }

    private String GetMountInfo(int idx) throws IOException {
        Process process = Runtime.getRuntime().exec("/bin/sh -c mount");
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        ArrayList<String> outlines = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            outlines.add(line);
            line = reader.readLine();
        }
        for (String drv: outlines) {
            String[] lnsplit = drv.split(" ");
            if (lnsplit[2].equals(this.mount)) {
                return lnsplit[idx];
            }
        }
        return "N/A";
    }

    public String GetFilesystem() throws IOException {
        return GetMountInfo(4);
    }
    public String GetDevice() throws IOException {
        return GetMountInfo(0);
    }

    public Boolean GetValid() {
        return this.isValid;
    }

    public String GetHash(String input) throws NoSuchAlgorithmException {
        // we use MD5 here for backwards compatibility reasons
        // the PIN code is kind of redundant anyway since a lot of this stuff
        // can be done without the flash drive control panel
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes(StandardCharsets.US_ASCII));
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    public String GetPin() {
        return this.pin;
    }

    public boolean ConvertInsecure(String newPin) {
        this.securePin = false;
        return this.SetPin(newPin);
    }
    public boolean ConvertSecure(String newPin) {
        this.securePin = true;
        return this.SetPin(newPin);
    }

    public void SetName(String newName) {
        try {
            File outputFileUnsecure = new File(this.mount + "/autorun.inf");
            PrintStream ptstr = new PrintStream(outputFileUnsecure, "ISO-8859-15");
            Runtime.getRuntime().addShutdownHook(new Thread(ptstr::close));
            ptstr.print("[Autorun]\r\n");
            ptstr.print("label=" + newName + "\r\n");
            ptstr.print("\r\n");
            ptstr.print("[Autorun.Amd64]\r\n");
            ptstr.print("label=" + newName + "\r\n");
            ptstr.flush();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean SetPin(String newPin) {
        try {
            if (this.securePin) {
                newPin = this.GetHash(newPin);
                File outputFileUnsecure = new File(this.mount + "/NTFS/config.sys");
                PrintStream ptstr = new PrintStream(outputFileUnsecure);
                Runtime.getRuntime().addShutdownHook(new Thread(ptstr::close));
                ptstr.println("Ebaturvaline PIN kood keelatud");
                ptstr.println("Insecure authentication code disabled");
                ptstr.flush();
            }
            File outputFile = new File(this.mount + "/NTFS/" + (this.securePin?"spin":"config") + ".sys");
            PrintStream printStr = null;
            printStr = new PrintStream(outputFile);
            Runtime.getRuntime().addShutdownHook(new Thread(printStr::close));
            printStr.println(newPin);
            printStr.flush();
            this.pin = newPin;
            return true;
        } catch (FileNotFoundException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    public Boolean VerifyPin(String pin) throws NoSuchAlgorithmException {
        if (this.securePin) {
            return this.pin.equals(GetHash(pin));
        } else {
            return pin.equals(this.pin);
        }
    }

    public List<String> GetUsers() throws IOException {
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path file) throws IOException {
                return (Files.isDirectory(file) && !String.valueOf(file.getFileName()).equals("Mine"));
            }
        };

        List<String> fldrs = new ArrayList<>();
        Path dir = FileSystems.getDefault().getPath(this.mount + "/markuse asjad/markuse asjad/");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
            for (Path path : stream) {
                // Iterate over the paths in the directory and print filenames
                fldrs.add(String.valueOf(path.getFileName()));
            }
        } catch (IOException e) {
            return fldrs;
        }
        return fldrs;
    }

    public List<String> GetVideos() {
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path file) throws IOException {
                return !Files.isDirectory(file) && file.getFileName().toString().contains(". ");
            }
        };

        List<String> fldrs = new ArrayList<>();
        Path dir = FileSystems.getDefault().getPath(this.mount + "/Markuse_videod/");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
            for (Path path : stream) {
                // Iterate over the paths in the directory and print filenames
                fldrs.add(String.valueOf(path.getFileName()));
            }
        } catch (IOException e) {
            return fldrs;
        }
        return fldrs;
    }

    public List<String> GetQApps() {
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path file) throws IOException {
                return Files.isDirectory(file) && !file.getFileName().toString().contains(" Mine");
            }
        };

        List<String> fldrs = new ArrayList<>();
        Path dir = FileSystems.getDefault().getPath(this.mount + "/markuse asjad/Kiirrakendused/");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
            for (Path path : stream) {
                // Iterate over the paths in the directory and print filenames
                fldrs.add(String.valueOf(path.getFileName()));
            }
        } catch (IOException e) {
            return fldrs;
        }
        return fldrs;
    }

    public String GetQAppDescription(String appName) {
        return readLine(this.mount + "/markuse asjad/Kiirrakendused/" + appName + "/" + appName + "Info.txt", StandardCharsets.UTF_16);
    }

    public boolean DoesQAppRequireWine(String appName) {
        File f = new File(this.mount + "/markuse asjad/Kiirrakendused/" + appName + "/" + appName + "Portable.AppImage");
        return !f.exists() || f.isDirectory();
    }

    public long CalcDirSize(String dirName) {
        String dir = this.mount + dirName;
        return this.CalculateFileSize(new File(dir));
    }

    public long CalculateFileSize(File file) {
        long fileSize = 0L;
        boolean isSymbolicLink = new Verifile(this.mount).cs(file);
        if(file.isDirectory() && !isSymbolicLink) {
            File[] children = file.listFiles();
            for(File child : children != null ? children : new File[0]) {
                fileSize += CalculateFileSize(child);
            }
        }
        else {
            fileSize = file.length();
        }
        return fileSize;
    }

    public String GetNiceSize(Long size) {
        DecimalFormat df = new DecimalFormat("###.##");
        if (size < 1000L) {
            return size + " B";
        } else if (size < 1000000L) {
            return df.format((float)size / 1000f) + " kB";
        } else if (size < 1000000000L) {
            return df.format((float)size / 1000000f) + " MB";
        } else if (size < 1000000000000L) {
            return df.format((float)size / 1000000000f) + " GB";
        } else if (size < 1000000000000000L) {
            return df.format((float)size / 1000000000000f) + " TB";
        } else if (size < 1000000000000000000L) {
            return df.format((float)size / 1000000000000000f) + " PB";
        } else {
            return df.format((float)size / 1000000000000000000f) + " EB";
        }
    }

    public void ReplaceVideo(String original, String replacedBy, String userName) throws IOException {
        File originalVideo = new File(this.mount + "/Markuse_videod/" + original);
        File newVideo = new File(replacedBy);
        String[] originalSplit = original.split("\\. ");
        String index = originalSplit[0];
        String safeName = originalSplit[1];
        Files.move(originalVideo.toPath(), Path.of(this.mount + "/markuse asjad/markuse asjad/" + userName + "/Minu videod/" + safeName));
        Files.move(newVideo.toPath(), Path.of(this.mount + "/Markuse_videod/" + index + ". " + newVideo.getName()));
        System.out.println(newVideo.getName());
    }
}
