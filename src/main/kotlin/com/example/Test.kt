
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun main(args: Array<String>) {
    val a = "1234"
    val keySpec = SecretKeySpec("1234567890123456".toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
    val ciphertext = cipher.doFinal(a.toByteArray())
    val encodedByte = Base64.getEncoder().encode(ciphertext)
    println(String(encodedByte))
}