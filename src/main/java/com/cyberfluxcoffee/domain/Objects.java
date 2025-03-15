package domain;

import java.util.concurrent.Semaphore;

public class Objects {
    private final Semaphore semaforo;

    public Objects(int quantidade) {
        this.semaforo = new Semaphore(quantidade, true); // Ordem justa para evitar starvation
        }
    public boolean tentarAlocar() {return semaforo.tryAcquire();}

    public void liberar() {semaforo.release();}

    public int getDisponiveis() {return semaforo.availablePermits();}

    public boolean temDisponivel() {return semaforo.availablePermits() > 0;}
}
