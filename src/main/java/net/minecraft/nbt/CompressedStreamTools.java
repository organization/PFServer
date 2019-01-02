package net.minecraft.nbt;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

import javax.annotation.Nullable;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedStreamTools
{
    public static NBTTagCompound readCompressed(InputStream is) throws IOException
    {
        NBTTagCompound nbttagcompound;
        try (DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(is)))) {

            nbttagcompound = read(datainputstream, NBTSizeTracker.INFINITE);
        }

        return nbttagcompound;
    }

    public static void writeCompressed(NBTTagCompound compound, OutputStream outputStream) throws IOException
    {

        try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)))) {
            write(compound, dataoutputstream);
        }
    }

    public static void safeWrite(NBTTagCompound compound, File fileIn) throws IOException
    {
        File file1 = new File(fileIn.getAbsolutePath() + "_tmp");

        if (file1.exists())
        {
            file1.delete();
        }

        write(compound, file1);

        if (fileIn.exists())
        {
            fileIn.delete();
        }

        if (fileIn.exists())
        {
            throw new IOException("Failed to delete " + fileIn);
        }
        else
        {
            file1.renameTo(fileIn);
        }
    }

    public static NBTTagCompound read(DataInputStream inputStream) throws IOException
    {
        return read(inputStream, NBTSizeTracker.INFINITE);
    }

    public static NBTTagCompound read(DataInput input, NBTSizeTracker accounter) throws IOException
    {
        NBTBase nbtbase = read(input, 0, accounter);

        if (nbtbase instanceof NBTTagCompound)
        {
            return (NBTTagCompound)nbtbase;
        }
        else
        {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(NBTTagCompound compound, DataOutput output) throws IOException
    {
        writeTag(compound, output);
    }

    private static void writeTag(NBTBase tag, DataOutput output) throws IOException
    {
        output.writeByte(tag.getId());

        if (tag.getId() != 0)
        {
            output.writeUTF("");
            tag.write(output);
        }
    }

    private static NBTBase read(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
    {
        byte b0 = input.readByte();
        accounter.read(8); // Forge: Count everything!

        if (b0 == 0)
        {
            return new NBTTagEnd();
        }
        else
        {
            NBTSizeTracker.readUTF(accounter, input.readUTF()); //Forge: Count this string.
            accounter.read(32); //Forge: 4 extra bytes for the object allocation.
            NBTBase nbtbase = NBTBase.createNewByType(b0);

            try
            {
                nbtbase.read(input, depth, accounter);
                return nbtbase;
            }
            catch (IOException ioexception)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
                crashreportcategory.addCrashSection("Tag type", b0);
                throw new ReportedException(crashreport);
            }
        }
    }

    public static void write(NBTTagCompound compound, File fileIn) throws IOException
    {

        try (DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(fileIn))) {
            write(compound, dataoutputstream);
        }
    }

    @Nullable
    public static NBTTagCompound read(File fileIn) throws IOException
    {
        if (!fileIn.exists())
        {
            return null;
        }
        else
        {
            NBTTagCompound nbttagcompound;
            try (DataInputStream datainputstream = new DataInputStream(new FileInputStream(fileIn))) {

                nbttagcompound = read(datainputstream, NBTSizeTracker.INFINITE);
            }

            return nbttagcompound;
        }
    }
}