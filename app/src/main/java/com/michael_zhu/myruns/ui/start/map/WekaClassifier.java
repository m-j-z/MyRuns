package com.michael_zhu.myruns.ui.start.map;

@SuppressWarnings("ALL")
class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N261f8ef80(i);
        return p;
    }
    static double N261f8ef80(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 56.569851) {
            p = WekaClassifier.N1a11d7d31(i);
        } else if (((Double) i[0]).doubleValue() > 56.569851) {
            p = WekaClassifier.N16ac14b13(i);
        }
        return p;
    }
    static double N1a11d7d31(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 0;
        } else if (((Double) i[10]).doubleValue() <= 1.228773) {
            p = 0;
        } else if (((Double) i[10]).doubleValue() > 1.228773) {
            p = WekaClassifier.N62bba1b02(i);
        }
        return p;
    }
    static double N62bba1b02(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 2;
        } else if (((Double) i[8]).doubleValue() <= 2.301146) {
            p = 2;
        } else if (((Double) i[8]).doubleValue() > 2.301146) {
            p = 0;
        }
        return p;
    }
    static double N16ac14b13(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 530.268018) {
            p = WekaClassifier.N4c9e30d24(i);
        } else if (((Double) i[0]).doubleValue() > 530.268018) {
            p = WekaClassifier.N722fae8f43(i);
        }
        return p;
    }
    static double N4c9e30d24(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 1;
        } else if (((Double) i[19]).doubleValue() <= 8.508785) {
            p = WekaClassifier.N5825d9dd5(i);
        } else if (((Double) i[19]).doubleValue() > 8.508785) {
            p = WekaClassifier.N27afbff641(i);
        }
        return p;
    }
    static double N5825d9dd5(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 172.608456) {
            p = WekaClassifier.N67ac45486(i);
        } else if (((Double) i[0]).doubleValue() > 172.608456) {
            p = WekaClassifier.N3186e90b24(i);
        }
        return p;
    }
    static double N67ac45486(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() <= 5.445408) {
            p = WekaClassifier.N11d3dc907(i);
        } else if (((Double) i[11]).doubleValue() > 5.445408) {
            p = 0;
        }
        return p;
    }
    static double N11d3dc907(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 72.785436) {
            p = WekaClassifier.N2c7666d88(i);
        } else if (((Double) i[0]).doubleValue() > 72.785436) {
            p = WekaClassifier.N50d0418b11(i);
        }
        return p;
    }
    static double N2c7666d88(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 6.303116) {
            p = WekaClassifier.N5aeba9ba9(i);
        } else if (((Double) i[2]).doubleValue() > 6.303116) {
            p = WekaClassifier.N5f6831aa10(i);
        }
        return p;
    }
    static double N5aeba9ba9(Object []i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 0;
        } else if (((Double) i[16]).doubleValue() <= 0.295507) {
            p = 0;
        } else if (((Double) i[16]).doubleValue() > 0.295507) {
            p = 1;
        }
        return p;
    }
    static double N5f6831aa10(Object []i) {
        double p = Double.NaN;
        if (i[32] == null) {
            p = 2;
        } else if (((Double) i[32]).doubleValue() <= 0.666177) {
            p = 2;
        } else if (((Double) i[32]).doubleValue() > 0.666177) {
            p = 0;
        }
        return p;
    }
    static double N50d0418b11(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 19.768445) {
            p = WekaClassifier.N4f8fadef12(i);
        } else if (((Double) i[1]).doubleValue() > 19.768445) {
            p = WekaClassifier.N29121d8119(i);
        }
        return p;
    }
    static double N4f8fadef12(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 1;
        } else if (((Double) i[13]).doubleValue() <= 3.117462) {
            p = WekaClassifier.N48ae7bdd13(i);
        } else if (((Double) i[13]).doubleValue() > 3.117462) {
            p = 0;
        }
        return p;
    }
    static double N48ae7bdd13(Object []i) {
        double p = Double.NaN;
        if (i[24] == null) {
            p = 2;
        } else if (((Double) i[24]).doubleValue() <= 0.093924) {
            p = 2;
        } else if (((Double) i[24]).doubleValue() > 0.093924) {
            p = WekaClassifier.N56d5e60314(i);
        }
        return p;
    }
    static double N56d5e60314(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 8.420317) {
            p = WekaClassifier.N4db294b15(i);
        } else if (((Double) i[4]).doubleValue() > 8.420317) {
            p = WekaClassifier.N74dee32018(i);
        }
        return p;
    }
    static double N4db294b15(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() <= 1.105915) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() > 1.105915) {
            p = WekaClassifier.N36090cea16(i);
        }
        return p;
    }
    static double N36090cea16(Object []i) {
        double p = Double.NaN;
        if (i[31] == null) {
            p = 0;
        } else if (((Double) i[31]).doubleValue() <= 0.530651) {
            p = 0;
        } else if (((Double) i[31]).doubleValue() > 0.530651) {
            p = WekaClassifier.Ndc2f96517(i);
        }
        return p;
    }
    static double Ndc2f96517(Object []i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 1;
        } else if (((Double) i[14]).doubleValue() <= 2.079292) {
            p = 1;
        } else if (((Double) i[14]).doubleValue() > 2.079292) {
            p = 0;
        }
        return p;
    }
    static double N74dee32018(Object []i) {
        double p = Double.NaN;
        if (i[31] == null) {
            p = 0;
        } else if (((Double) i[31]).doubleValue() <= 0.13106) {
            p = 0;
        } else if (((Double) i[31]).doubleValue() > 0.13106) {
            p = 1;
        }
        return p;
    }
    static double N29121d8119(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() <= 2.366447) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() > 2.366447) {
            p = WekaClassifier.N57823a1520(i);
        }
        return p;
    }
    static double N57823a1520(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 9.927198) {
            p = WekaClassifier.N4d91e85121(i);
        } else if (((Double) i[7]).doubleValue() > 9.927198) {
            p = WekaClassifier.N7792277e23(i);
        }
        return p;
    }
    static double N4d91e85121(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 0;
        } else if (((Double) i[15]).doubleValue() <= 1.69728) {
            p = WekaClassifier.N4e55c52022(i);
        } else if (((Double) i[15]).doubleValue() > 1.69728) {
            p = 1;
        }
        return p;
    }
    static double N4e55c52022(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() <= 8.990027) {
            p = 0;
        } else if (((Double) i[6]).doubleValue() > 8.990027) {
            p = 1;
        }
        return p;
    }
    static double N7792277e23(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 158.021127) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 158.021127) {
            p = 0;
        }
        return p;
    }
    static double N3186e90b24(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 39.721728) {
            p = WekaClassifier.N73975c0a25(i);
        } else if (((Double) i[3]).doubleValue() > 39.721728) {
            p = WekaClassifier.N762950e830(i);
        }
        return p;
    }
    static double N73975c0a25(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 53.198632) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 53.198632) {
            p = WekaClassifier.N130bfb9d26(i);
        }
        return p;
    }
    static double N130bfb9d26(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() <= 12.56991) {
            p = WekaClassifier.N7544875527(i);
        } else if (((Double) i[9]).doubleValue() > 12.56991) {
            p = 1;
        }
        return p;
    }
    static double N7544875527(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() <= 8.126485) {
            p = WekaClassifier.N738ce64728(i);
        } else if (((Double) i[10]).doubleValue() > 8.126485) {
            p = 2;
        }
        return p;
    }
    static double N738ce64728(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 1;
        } else if (((Double) i[13]).doubleValue() <= 2.986317) {
            p = WekaClassifier.N3ab2833029(i);
        } else if (((Double) i[13]).doubleValue() > 2.986317) {
            p = 1;
        }
        return p;
    }
    static double N3ab2833029(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() <= 4.939775) {
            p = 1;
        } else if (((Double) i[9]).doubleValue() > 4.939775) {
            p = 2;
        }
        return p;
    }
    static double N762950e830(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 2;
        } else if (((Double) i[15]).doubleValue() <= 0.581344) {
            p = 2;
        } else if (((Double) i[15]).doubleValue() > 0.581344) {
            p = WekaClassifier.N27861f5731(i);
        }
        return p;
    }
    static double N27861f5731(Object []i) {
        double p = Double.NaN;
        if (i[24] == null) {
            p = 1;
        } else if (((Double) i[24]).doubleValue() <= 4.463549) {
            p = WekaClassifier.N33ac7b3e32(i);
        } else if (((Double) i[24]).doubleValue() > 4.463549) {
            p = WekaClassifier.N303d18fb40(i);
        }
        return p;
    }
    static double N33ac7b3e32(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 9.415979) {
            p = WekaClassifier.N1d29c57833(i);
        } else if (((Double) i[64]).doubleValue() > 9.415979) {
            p = WekaClassifier.N3650b7f235(i);
        }
        return p;
    }
    static double N1d29c57833(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 2;
        } else if (((Double) i[13]).doubleValue() <= 4.100581) {
            p = WekaClassifier.N213b682534(i);
        } else if (((Double) i[13]).doubleValue() > 4.100581) {
            p = 1;
        }
        return p;
    }
    static double N213b682534(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 9.866844) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() > 9.866844) {
            p = 2;
        }
        return p;
    }
    static double N3650b7f235(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 58.680173) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() > 58.680173) {
            p = WekaClassifier.N66f54f8d36(i);
        }
        return p;
    }
    static double N66f54f8d36(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 132.369967) {
            p = WekaClassifier.N3524542e37(i);
        } else if (((Double) i[2]).doubleValue() > 132.369967) {
            p = 2;
        }
        return p;
    }
    static double N3524542e37(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 2;
        } else if (((Double) i[13]).doubleValue() <= 1.021327) {
            p = 2;
        } else if (((Double) i[13]).doubleValue() > 1.021327) {
            p = WekaClassifier.N4fa468ef38(i);
        }
        return p;
    }
    static double N4fa468ef38(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 125.263012) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 125.263012) {
            p = WekaClassifier.N7cf8b9ce39(i);
        }
        return p;
    }
    static double N7cf8b9ce39(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() <= 67.285846) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() > 67.285846) {
            p = 1;
        }
        return p;
    }
    static double N303d18fb40(Object []i) {
        double p = Double.NaN;
        if (i[28] == null) {
            p = 2;
        } else if (((Double) i[28]).doubleValue() <= 5.096336) {
            p = 2;
        } else if (((Double) i[28]).doubleValue() > 5.096336) {
            p = 1;
        }
        return p;
    }
    static double N27afbff641(Object []i) {
        double p = Double.NaN;
        if (i[17] == null) {
            p = 2;
        } else if (((Double) i[17]).doubleValue() <= 10.699599) {
            p = 2;
        } else if (((Double) i[17]).doubleValue() > 10.699599) {
            p = WekaClassifier.N4ba048fc42(i);
        }
        return p;
    }
    static double N4ba048fc42(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() <= 18.346264) {
            p = 1;
        } else if (((Double) i[6]).doubleValue() > 18.346264) {
            p = 2;
        }
        return p;
    }
    static double N722fae8f43(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 704.123378) {
            p = WekaClassifier.N2a0d9af144(i);
        } else if (((Double) i[0]).doubleValue() > 704.123378) {
            p = 2;
        }
        return p;
    }
    static double N2a0d9af144(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() <= 65.285155) {
            p = WekaClassifier.N7007d04945(i);
        } else if (((Double) i[5]).doubleValue() > 65.285155) {
            p = WekaClassifier.N7061c54d48(i);
        }
        return p;
    }
    static double N7007d04945(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 2;
        } else if (((Double) i[8]).doubleValue() <= 18.423484) {
            p = 2;
        } else if (((Double) i[8]).doubleValue() > 18.423484) {
            p = WekaClassifier.N3929811d46(i);
        }
        return p;
    }
    static double N3929811d46(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 19.720196) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() > 19.720196) {
            p = WekaClassifier.N367d0b6147(i);
        }
        return p;
    }
    static double N367d0b6147(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 542.910441) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 542.910441) {
            p = 2;
        }
        return p;
    }
    static double N7061c54d48(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 124.624722) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() > 124.624722) {
            p = 2;
        }
        return p;
    }
}