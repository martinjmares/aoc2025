package name.mjm.aoc.y2015;

import name.mjm.aoc.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Datas({
    @Data("yzbqklnj"),
    @Data(tryId = 1, value = "abcdef"),
    @Data(tryId = 2, value = "pqrstuv")
})
public class Day04 extends ParentDay {

  static final byte ZERO = "0".getBytes()[0];

  @TryResults({
      @TryResult(tryId = 1, value = "609043"),
      @TryResult(tryId = 2, value = "1048970")
  })
  public long a(String prefix) throws NoSuchAlgorithmException {
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    for (int i = 1; true; i++) {
      String val = prefix + i;
      byte[] digest = md5.digest(val.getBytes());
      if (digest[0] == 0 && digest[1] == 0 && (digest[2] & 0xF0) == 0) {
        logger.info("Solution is for " + val + " with digest " + HexFormat.of().formatHex(digest));
        return i;
      }
    }
  }

  public long b(String prefix) throws NoSuchAlgorithmException {
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    for (int i = 1; true; i++) {
      String val = prefix + i;
      byte[] digest = md5.digest(val.getBytes());
      if (digest[0] == 0 && digest[1] == 0 && digest[2] == 0) {
        logger.info("Solution is for " + val + " with digest " + HexFormat.of().formatHex(digest));
        return i;
      }
    }
  }

}
