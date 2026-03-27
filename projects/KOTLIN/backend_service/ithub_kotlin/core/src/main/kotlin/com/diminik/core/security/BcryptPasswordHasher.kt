package com.diminik.core.security

import com.diminik.domain.ports.PasswordHasher
import org.mindrot.jbcrypt.BCrypt

class BcryptPasswordHasher : PasswordHasher {
    override fun hash(rawPassword: String): String = BCrypt.hashpw(rawPassword, BCrypt.gensalt())

    override fun matches(rawPassword: String, passwordHash: String): Boolean = BCrypt.checkpw(rawPassword, passwordHash)
}
