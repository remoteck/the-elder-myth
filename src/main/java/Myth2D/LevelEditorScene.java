package Myth2D;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;
    private float[] vertexArray = {
            //Posição                    //Cor
            0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, //Inferior direito — 0
            -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, //Superior esquerdo — 1
            0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, //Superior direito — 2
            -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f  //Inferior esquerdo — 3
    };
    //IMPORTANTE: Ordem em sentido anti-horário
    private int[] elementArray = {
            2, 1, 0, //Triangulo superior direito
            0, 1, 3  //Triangulo inferior esquerdo
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        //Compilar e linkar os shaders de cor
        //Carregar e compilar os shaders de vértices
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //Linkar a fonte dos shaders com a placa de vídeo
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);
        //Checar por erros na compilação (mostra 0 caso tenha erros)
        int successV = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (successV == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERRO: defaultShader.glsl\n\tFalha ao compilar os shaders de vértices.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        //Carregar e compilar os shaders de vértices
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //Linkar a fonte dos shaders com a placa de vídeo
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);
        //Checar por erros na compilação (mostra 0 caso tenha erros)
        int successF = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (successF == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERRO: defaultShader.glsl\n\tFalha ao compilar os shaders de fragmento.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        //Linkar os shaders e checar por erros
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);
        int successP = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (successP == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERRO: defaultShader.glsl\n\tFalha ao linkar os shaders.");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

        //Gerar os buffers e mandar para placa de vídeo
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Criar um buffer tipo float por que o OpenGL precisa desse float
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //Criar os indices e upar
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //Adicionar os ponteiros dos atributos dos vértices
        int positionSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        //Informar o programa de shader
        glUseProgram(shaderProgram);
        //Informar o VAO
        glBindVertexArray(vaoID);
        //Habilitar os ponteiros dos atributos dos vértices
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        //Desenhar
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
        //Desconectar tudo
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glUseProgram(0);
    }
}
