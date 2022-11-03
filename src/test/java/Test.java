
import com.ssa.cms.util.DateUtil;
import com.ssa.cms.util.EncryptionHandlerUtil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Javed
 */
public class Test {
            public static void main(String[] args) throws Exception{
        String encrypted = "epcOT6Yv27JoZ30u0YaS6A==";
        String dycrepted = "2488029559";

        System.out.println(EncryptionHandlerUtil.getDecryptedString(encrypted));
        System.out.println(EncryptionHandlerUtil.getEncryptedString(dycrepted));
//         String birth = "1990";
//         Date
//         Date todayDate = new Date();
//         String d = Integer.toString(todayDate);
//        System.out.println("birth: "+birth);
//        System.out.println("d: "+d);
//        System.out.println("yearrrrrrrr: "+ birth - d);
//     System.out.println(DateUtil.changeDateFormat("2019-01-01 11:34:07.0", "yyyy-MM-dd mm:HH:ss", "dd"));
    }
}
