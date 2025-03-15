package domain;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Client extends Thread {
    private static final AtomicInteger contadorId = new AtomicInteger(0);
    private final PolicyManager policyManager;
    private final FinalStatistics finalStatistics;
    private final Random random = new Random();

    private final int tipo;
    private final int id;
    private final long chegada;
    private static volatile boolean simulacaoEncerrada = false;
    private boolean usouPC = false;
    private boolean usouVR = false;
    private boolean usouCadeira = false;
    private final Semaphore semaforoEspera = new Semaphore(0); //  Semáforo para esperar na fila

    public Client(int tipo, PolicyManager policyManager, FinalStatistics finalStatistics) {
        this.tipo = tipo;
        this.id = gerarId();
        this.chegada = System.currentTimeMillis();
        this.policyManager = policyManager;
        this.finalStatistics = finalStatistics;
    }

    private static int gerarId() {return contadorId.getAndIncrement();}

    public int getIdClient() {
        return id;
    }

    public long getChegada() {
        return chegada;
    }

    public int getTipo() {
        return tipo;
    }

    // Métodos para marcar quais recursos foram alocados
    public void setUsouPC(boolean valor) {usouPC = valor;}

    public void setUsouVR(boolean valor) {usouVR = valor;}

    public void setUsouCadeira(boolean valor) {usouCadeira = valor;}

    // Métodos para verificar quais recursos o cliente usou
    public boolean usouPC() {return usouPC;}

    public boolean usouVR() {return usouVR;}

    public boolean usouCadeira() {return usouCadeira;}

    public static void encerrarSimulacao() {simulacaoEncerrada = true;}

    public void aguardarNaFila() {
        try {
            while (true) { // Loop infinito até ser notificado
                if (semaforoEspera.tryAcquire(10, TimeUnit.MILLISECONDS)) {
                    return; // Cliente foi notificado e pode sair da espera
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();}
    }

    public void notificarCliente() {
        semaforoEspera.release(); //  Libera o cliente da espera
    }

    @Override
    public void run() {
        if (simulacaoEncerrada) return;
        boolean conseguiu;

        while (!simulacaoEncerrada) {
            try {
                // Tenta alocar o PC inicial
                conseguiu = policyManager.tentarAlocarRecursos(this);

                if (!conseguiu) {
                    // Se falhou, entra na fila e espera notificação
                    policyManager.addFila(this);
                    aguardarNaFila();
                    continue; // Tenta novamente após esperar
                     }
                //System.out.println("🚨 " + getTipoString() + " " + id + " conseguiu PC...");

                if (tipo != 2) {
                    // Agora tenta alocar o recurso opcional (cadeira ou VR) por 10 segundos
                    boolean conseguiuOpcional = false;
                    long tempoMaxOpcional = System.currentTimeMillis() + 10000; // 10 segundos

                    while (System.currentTimeMillis() < tempoMaxOpcional && !conseguiuOpcional) {
                        //System.out.println("🚨 " + getTipoString() + " " + id + " tentando alocar recurso opcional...");
                        conseguiuOpcional = policyManager.tentarAlocarOpcional(this);
                        if (!conseguiuOpcional) {
                            Thread.sleep(50);}
                    }

                    if (!conseguiuOpcional) {
                        //System.out.println("🚨 " + getTipoString() + " " + id + " não conseguiu recurso opcional, desistindo...");
                        policyManager.liberarRecursos(this);
                        policyManager.addFila(this); // Volta para a fila
                        aguardarNaFila(); // Espera novamente
                        continue; // Volta para a fila
                    }
                }
                //Conseguiu tudo que precisava, se ele estiver na fila, remove
                policyManager.removerDaFila(this);

                // Se conseguiu tudo, simula o uso
                System.out.println("\uD83D\uDE80 Cliente " + id + " usando recursos... " + getTipoString());
                finalStatistics.registrarEntrada();
                if (usouPC) finalStatistics.registrarUsoPC();
                if (usouVR) finalStatistics.registrarUsoVR();
                if (usouCadeira) finalStatistics.registrarUsoCadeira();
                long tempoEspera = System.currentTimeMillis() - chegada; // Tempo que esperou
                finalStatistics.registrarEspera(tempoEspera); // Registra na estatística
                simularUso();

                // Após o uso, libera os recursos
                policyManager.liberarRecursos(this);
                System.out.println("🚨 Cliente " + id + " liberando recursos...");

                return; // Sai do loop após utilizar o recurso
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;}
        }
    }

    private void simularUso() {
        try {
            System.out.println("🚨 Cliente " + id + " simulando uso...");
            int tempoUso = 15_000 + random.nextInt(45_000);
            while (tempoUso > 0 && !simulacaoEncerrada) {
                Thread.sleep(1000);
                tempoUso -= 1000;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();}
    }

    private String getTipoString() {
        return tipo == 0 ? "(Gamer)" : tipo == 1 ? "(Freelancer)" : "(Estudante)";
    }
}
