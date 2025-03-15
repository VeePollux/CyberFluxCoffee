package domain;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class FinalStatistics {
    private int clientesAtendidos = 0;
    private int clientesRecusados = 0;
    private long totalEspera = 0;
    private int totalPedidos = 0;
    private int totalUsosPC = 0;
    private int totalUsosVR = 0;
    private int totalUsosCadeira = 0;

    private final Semaphore semaforoEstatisticas = new Semaphore(1);

    public void registrarEntrada() {
        try {
            semaforoEstatisticas.acquire();
            clientesAtendidos++;
            totalPedidos++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforoEstatisticas.release();}
    }

    public void registrarRecusa() {
        try {
            semaforoEstatisticas.acquire();
            clientesRecusados++;
            totalPedidos++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforoEstatisticas.release();}
    }

    public void registrarEspera(long tempoEspera) {
        try {
            semaforoEstatisticas.acquire();
            totalEspera += tempoEspera;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforoEstatisticas.release();}
    }

    public void registrarUsoPC() {
        try {
            semaforoEstatisticas.acquire();
            totalUsosPC++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforoEstatisticas.release();}
    }

    public void registrarUsoVR() {
        try {
            semaforoEstatisticas.acquire();
            totalUsosVR++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforoEstatisticas.release();}
    }

    public void registrarUsoCadeira() {
        try {
            semaforoEstatisticas.acquire();
            totalUsosCadeira++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaforoEstatisticas.release();}
    }

    public void gerarRelatorio() {
        System.out.println("\n===== RELATÓRIO FINAL =====");
        System.out.println("✅ Clientes atendidos: " + clientesAtendidos);
        System.out.println("❌ Clientes recusados: " + clientesRecusados);
        System.out.println("\uD83D\uDCCC Total de pedidos: " + totalPedidos);
        System.out.println("⏳ Tempo médio de espera: " + (clientesAtendidos == 0 ? 0 : totalEspera / clientesAtendidos) + " ms");

        // Evita divisão por zero
        double taxaPC = (clientesAtendidos == 0) ? 0 : ((double) totalUsosPC / totalPedidos) * 100;
        double taxaVR = (clientesAtendidos == 0) ? 0 : ((double) totalUsosVR / totalPedidos) * 100;
        double taxaCadeira = (clientesAtendidos == 0) ? 0 : ((double) totalUsosCadeira / totalPedidos) * 100;

        System.out.println("\n\uD83D\uDDA5\uFE0F Uso de PCs: " + totalUsosPC + " vezes (" + String.format("%.2f", taxaPC) + "%)");
        System.out.println("\uD83E\uDD7D Uso de VRs: " + totalUsosVR + " vezes (" + String.format("%.2f", taxaVR) + "%)");
        System.out.println("\uD83D\uDCBA Uso de cadeiras: " + totalUsosCadeira + " vezes (" + String.format("%.2f", taxaCadeira) + "%)");

        gerarSaidaRelatorio();
    }

    public void gerarSaidaRelatorio() {
        String nomeArquivo = "relatorio_simulacao.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("===== RELATÓRIO FINAL =====");
            writer.println("Clientes atendidos: " + clientesAtendidos);
            writer.println("Clientes recusados: " + clientesRecusados);
            writer.println("Total de pedidos: " + totalPedidos);
            writer.println("Tempo médio de espera: " + (clientesAtendidos == 0 ? 0 : totalEspera / clientesAtendidos) + " ms");

            // Evita divisão por zero
            double taxaPC = (clientesAtendidos == 0) ? 0 : ((double) totalUsosPC / totalPedidos) * 100;
            double taxaVR = (clientesAtendidos == 0) ? 0 : ((double) totalUsosVR / totalPedidos) * 100;
            double taxaCadeira = (clientesAtendidos == 0) ? 0 : ((double) totalUsosCadeira / totalPedidos) * 100;

            writer.println("Uso de PCs: " + totalUsosPC + " vezes (" + String.format("%.2f", taxaPC) + "%)");
            writer.println("Uso de VRs: " + totalUsosVR + " vezes (" + String.format("%.2f", taxaVR) + "%)");
            writer.println("Uso de cadeiras: " + totalUsosCadeira + " vezes (" + String.format("%.2f", taxaCadeira) + "%)");

            System.out.println("\n📄 Relatório salvo em: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o relatório: " + e.getMessage());
        }
    }
}
