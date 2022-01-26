package Myth2D;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width, height;
    private String title;
    private long glfwWindow;
    public float r, g, b, a;
    private static Window window = null;
    private static Scene currentScene;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "The Elder Myth";
        r = 1.0f;
        g = 1.0f;
        b = 1.0f;
        a = 1.0f;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                //currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Cena desconhecida '" + newScene + "'";
                break;
        }
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public void run() {
        System.out.println("Olá, essa é a Engine Myth2D versão " + Version.getVersion());

        init();
        loop();

        //Liberar a memória
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Fechar o GLFW e liberar o erro de callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        //Colocar um callback de erro
        GLFWErrorCallback.createPrint(System.err).set();

        //Inicializar o GLFW para abrir a janela caso não haja erro
        if (!glfwInit()) {
            throw new IllegalStateException("Não foi possível inicializar o GLFW.");
        }

        //Configurar o GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //Criar a janela
        //Esse passo precisa de memory management, por que se baseia em C
        //Por isso essa variável é do tipo Long, identifica um espaço de memória
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Falha ao criar a janela GLFW.");
        }

        //Lambda para registrar callback de cursor
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //Fazer o link com OpenGL
        glfwMakeContextCurrent(glfwWindow);
        //Habilitar V-Sync
        glfwSwapInterval(1);

        //Mostar a janela
        glfwShowWindow(glfwWindow);
        //Essa linha é crítica para a interação do framework com o OpenGL
        //GLCapabilities cria uma instância e permite a conexão do OpenGL
        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            //Captar eventos de mouse, teclas e etc
            glfwPollEvents();
            //Cor
            glClearColor(r, g, b, a);
            //Usar o Color Buffer para carregar as cores colocadas acima
            glClear(GL_COLOR_BUFFER_BIT);
            if (dt >= 0)
                currentScene.update(dt);
            //Trocar os buffer, OpenGL e GLFW fazem automaticamente
            glfwSwapBuffers(glfwWindow);
            //Calcular delta tempo para detecção de frames e possíveis lags
            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

}
