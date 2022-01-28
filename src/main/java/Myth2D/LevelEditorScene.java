package Myth2D;

import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

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
    private Shader defaultShader;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

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
        defaultShader.use();
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
        defaultShader.detach();
    }
}
