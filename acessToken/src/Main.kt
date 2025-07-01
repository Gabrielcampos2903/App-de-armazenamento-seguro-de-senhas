import java.util.Base64
import java.security.SecureRandom

fun generateAccessToken(): String {
    val randomBytes = ByteArray(192) // 192 bytes aleatórios
    SecureRandom().nextBytes(randomBytes) // Preenche com valores aleatórios
    return Base64.getEncoder().encodeToString(randomBytes) // Converte para Base64
}

fun main() {
    val token = generateAccessToken()
    println(token) // Exibe o token gerado
}