import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class App {
    public static void main(String[] args) throws Exception {
        Carro.INTERVALO_DE_TEMPO = 0.1;
        Carro.SLEEP = 1000;
        Carro.SLEEP_SHOW = 0.0000000000001;
        /**
         * https://www.noticiasautomotivas.com.br/vw-up-tsi-versoes/
         * Consumo de 14,1 km/l na cidade e 16 km/l na estrada com gasolina;
         * Aceleração de 0 a 100 km/h em 9,3 segundos com gasolina;
         * Velocidade máxima de 182 km/h com gasolina.
         * Tanque 50 litros
         */
        double segundos = 9.3;
        int kmph = 100;
        double taxaDeAceleracao = Util.getAceleracao(0, Util.toMpS(kmph), 0, segundos);
        taxaDeAceleracao = BigDecimal.valueOf(taxaDeAceleracao).setScale(1, RoundingMode.UP).doubleValue();
        Carro upTSI = new Carro("Volkswagen Up TSI 1.0", 50, 10, 180, taxaDeAceleracao);

        segundos = 2.3;
        kmph = 99;
        double a2 = Util.getAceleracao(0, Util.toMpS(kmph), 0, segundos);
        a2 = BigDecimal.valueOf(a2).setScale(1, RoundingMode.UP).doubleValue();
        Carro bugatti = new Carro("Bugatti Veyron", 50, 7, 410, a2);
        try {
            boolean mostrarUP = true;
            if (mostrarUP) {
                upTSI.acaoAbastecer(7, 7.8);
                Thread.sleep(2000);
                upTSI.acaoAcelerar(16.66667);// t para 180 km/h, distancia 0.416667m
                upTSI.acaoPilotoAutomaticoKM(180, 9.16666);
                Thread.sleep(2500);
                upTSI.parar();
                Thread.sleep(3500);
                upTSI.acaoFrear(9.5);
                upTSI.acaoPilotoAutomaticoKM(90, 1);

                Thread.sleep(2000);
                System.out.println();
                System.out.println();
            }
            bugatti.acaoAbastecer(7, 3.5);
            bugatti.acaoAcelerar(21);
            bugatti.parar();

        } catch (SemCombustivel e) {
            System.out.println();
            System.out.println(e.getMessage());
        } catch (NotNegative e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void limparTela() {
        // Limpa a tela no windows, no linux e no MacOS
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (InterruptedException e) {
        } catch (IOException e) {
        }
    }
}
