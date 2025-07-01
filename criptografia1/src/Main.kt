import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec

object SimpleCrypto {

    // Chave fixa de 16 bytes (128 bits) — para testes!
    private const val SECRET_KEY = "1234567890123456" // NÃO use isso em produção!
    private const val INIT_VECTOR = "abcdefghijklmnop" // Também 16 bytes

    fun encrypt(input: String): String {
        val iv = IvParameterSpec(INIT_VECTOR.toByteArray(Charsets.UTF_8))
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        val encrypted = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(encrypted: String): String {
        val iv = IvParameterSpec(INIT_VECTOR.toByteArray(Charsets.UTF_8))
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
        val decoded = Base64.decode(encrypted, Base64.DEFAULT)
        val original = cipher.doFinal(decoded)
        return String(original, Charsets.UTF_8)
    }
}

/* val senha = "minhaSenhaSuperSegura"
val senhaCriptografada = SimpleCrypto.encrypt(senha)
val senhaDescriptografada = SimpleCrypto.decrypt(senhaCriptografada)

println("Criptografada: $senhaCriptografada")
println("Descriptografada: $senhaDescriptografada")
        */
