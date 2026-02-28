package kotlin2d

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_CLAMP
import org.lwjgl.opengl.GL11.GL_LINEAR
import org.lwjgl.opengl.GL11.GL_RGBA
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S
import org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glDeleteTextures
import org.lwjgl.opengl.GL11.glTexImage2D
import org.lwjgl.opengl.GL11.glTexParameteri
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack

data class Texture(
    val id: Int,
    val width: Int,
    val height: Int
) {
    fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun dispose() {
        glDeleteTextures(id)
    }

    companion object {
        fun load(resourcePath: String): Texture {
            val bytes = Texture::class.java.getResourceAsStream(resourcePath)?.use { input ->
                input.readAllBytes()
            } ?: error("Texture resource not found: $resourcePath")

            val imageBuffer = BufferUtils.createByteBuffer(bytes.size)
            imageBuffer.put(bytes).flip()

            MemoryStack.stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                val comp = stack.mallocInt(1)

                stbi_set_flip_vertically_on_load(false)
                val pixels = stbi_load_from_memory(imageBuffer, w, h, comp, 4)
                    ?: error("Failed to load image: ${stbi_failure_reason()}")

                val texId = glGenTextures()
                glBindTexture(GL_TEXTURE_2D, texId)

                glTexImage2D(
                    GL_TEXTURE_2D,
                    0,
                    GL_RGBA,
                    w[0],
                    h[0],
                    0,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    pixels
                )

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP)

                stbi_image_free(pixels)

                return Texture(texId, w[0], h[0])
            }
        }
    }
}

