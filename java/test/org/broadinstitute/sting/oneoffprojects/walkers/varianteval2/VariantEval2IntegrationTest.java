package org.broadinstitute.sting.oneoffprojects.walkers.varianteval2;

import org.broadinstitute.sting.WalkerTest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.io.File;

public class VariantEval2IntegrationTest extends WalkerTest {
    private static String cmdRoot = "-T VariantEval2" +
            " -R " + oneKGLocation + "reference/human_b36_both.fasta";

    private static String root = cmdRoot +
            " -D " + GATKDataLocation + "dbsnp_129_b36.rod" +
            " -B eval,VCF," + validationDataLocation + "yri.trio.gatk_glftrio.intersection.annotated.filtered.chr1.vcf";

    @Test
    public void testVE2Simple() {
        HashMap<String, String> expectations = new HashMap<String, String>();
        expectations.put("-L 1:1-10,000,000", "32b2e9758078b66e6d50d140acb37947");
        expectations.put("-L 1:1-10,000,000 -family NA19238+NA19239=NA19240 -MVQ 0", "5ee420ebf7c2d3c2e3827c0114a6706d");

        for ( Map.Entry<String, String> entry : expectations.entrySet() ) {
            String extraArgs = entry.getKey();
            String md5 = entry.getValue();

            WalkerTestSpec spec = new WalkerTestSpec( root + " " + extraArgs + " -o %s",
                    1, // just one output file
                    Arrays.asList(md5));
            executeTest("testVE2Simple", spec);
        }
    }

    @Test
    public void testVE2Complex() {
        HashMap<String, String> expectations = new HashMap<String, String>();
        String extraArgs1 = "-L " + validationDataLocation + "chr1_b36_pilot3.interval_list -family NA19238+NA19239=NA19240 -MVQ 30" +
                " -B dbsnp_130,dbSNP," + GATKDataLocation + "dbsnp_130_b36.rod" +
                " -B comp_hapmap,VCF," + validationDataLocation + "CEU_hapmap_nogt_23.vcf";

        String eqMD5s = "ba021a4c963200191710a220a5577753"; // next two examples should be the same!
        expectations.put("", eqMD5s);
        expectations.put(" -known comp_hapmap -known dbsnp", eqMD5s);
        expectations.put(" -known comp_hapmap", "5ce16165f4242d77b4e82c704273c11d");

        for ( Map.Entry<String, String> entry : expectations.entrySet() ) {
            String extraArgs2 = entry.getKey();
            String md5 = entry.getValue();

            WalkerTestSpec spec = new WalkerTestSpec( root + " " + extraArgs1 + extraArgs2 + " -o %s",
                    1, // just one output file
                    Arrays.asList(md5));
            executeTest("testVE2Complex", spec);
        }
    }

    @Test
    public void testVE2WriteVCF() {
        String extraArgs = "-L 1:1-10,000,000 -family NA19238+NA19239=NA19240 -MVQ 30";
        WalkerTestSpec spec = new WalkerTestSpec( root + " " + extraArgs + " -o %s -outputVCF %s",
                2,
                Arrays.asList("0b29285da3ca778b9c8b7f62e99aa72d", "d41d8cd98f00b204e9800998ecf8427e"));
        executeTest("testVE2WriteVCF", spec);
    }
}
