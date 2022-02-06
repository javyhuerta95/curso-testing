package com.javyhuerta.app.models;

import com.javyhuerta.app.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    Cuenta cuenta;
    // before all se ejecuta al inicio de todo una sola vez
    // after all se ejecuta al final de todo una sola vez


    @BeforeAll
    static void beforeAll() {
        System.out.println("Iniciando los test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando todos los test");
    }

    @BeforeEach
    void initTest(){
        System.out.println("Iniciando el metodo por cada test");
        cuenta =new Cuenta("Javy", new BigDecimal("150.69"));
    }

    @AfterEach
    void afterEach() {
        System.out.println("Finalizando el metodo por cada test");
    }

    @Tag("Cuenta")
    @Nested
    @DisplayName("Probando atributos")
    class cuentaNombreSaldoTest{
        @Test
        @DisplayName("el nombre de la cuenta corriente")
        void nombreCuenta() {
            String esperado = "Javy";
            String real = cuenta.getPersona();
            assertNotNull(real, ()-> "La cuenta no puede ser nula");
            assertEquals(esperado,real,() -> "El nombre de la cuenta no es el esperado");
            assertTrue(real.equals("Javy"),() -> "El nombre de la cuenta debe de ser igual a la real");
        }

        @Test
        @DisplayName("el saldo que no sea null, mayor que cero, valor esperado")
        void saldoCuenta() {
            assertEquals(150.69, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        }

        @Test
        @Disabled
        void referenciaCuenta() {
            cuenta = new Cuenta("Javy", new BigDecimal("9000.302"));
            Cuenta cuenta2 = new Cuenta("Javy", new BigDecimal("9000.302"));

            // Compara por referencia
            assertNotEquals(cuenta,cuenta2);
        }



        @Test
        void dineroInsuficienteException() {
            cuenta = new Cuenta("Javy", new BigDecimal("1000.302"));
            Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
                cuenta.debito(new BigDecimal("1500.302"));
            });

            assertEquals(exception.getMessage(),"Dinero Insuficiente");

        }



        @Test
        void relacionBancoCuenta() {
            Cuenta cuenta1 = new Cuenta("Javy", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Karen", new BigDecimal("1500.8989"));
            Banco banco = new Banco();
            banco.setName("Banco del noroeste");
            banco.addCuenta(cuenta1);
            banco.addCuenta(cuenta2);

            banco.transferir(cuenta2,cuenta1, new BigDecimal(500));

            assertAll(
                    () -> assertEquals("1000.8989",cuenta2.getSaldo().toPlainString()),
                    () -> assertEquals("3000",cuenta1.getSaldo().toPlainString()),
                    () -> assertEquals(2,banco.getCuentas().size()),
                    () -> assertEquals("Banco del noroeste",cuenta1.getBanco().getName()),
                    () -> assertEquals("Javy",banco.getCuentas().stream()
                            .filter(ctl -> ctl.getPersona().equals("Javy"))
                            .findFirst()
                            .get().getPersona())
            );
        }
    }

    @Nested
    class CuentaOperacionesTest{
        @Test
        void debitoCuenta() {
            cuenta = new Cuenta("Javy", new BigDecimal("1000.302"));
            cuenta.debito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900,cuenta.getSaldo().intValue());
            assertEquals("900.302",cuenta.getSaldo().toPlainString());
        }
        @Test
        void creditoCuenta() {
            cuenta = new Cuenta("Javy", new BigDecimal("1000.302"));
            cuenta.credito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100,cuenta.getSaldo().intValue());
            assertEquals("1100.302",cuenta.getSaldo().toPlainString());
        }

        @Test
        void transferirDineroCuenta() {
            Cuenta cuenta1 = new Cuenta("Javy", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Karen", new BigDecimal("1500.8989"));
            Banco banco = new Banco();
            banco.setName("Banco del noroeste");

            banco.transferir(cuenta2,cuenta1, new BigDecimal(500));

            assertEquals("1000.8989",cuenta2.getSaldo().toPlainString());
            assertEquals("3000",cuenta1.getSaldo().toPlainString());

        }
    }



    @Nested
    class SistemaOperativoTest{
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void soloWindows() {

        }

        @Test
        @EnabledOnOs({OS.LINUX,OS.MAC})
        void macLinux() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void noWindows() {
        }
    }

    @Nested
    class JavaVersionTest{
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void solojdk8() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_15)
        void noJdk15() {
        }
    }

    @Nested
    class SystemPropertiesTest{
        @Test
        void imrpimirSystemProperties() {
            Properties properties = System.getProperties();
            System.out.println();
            properties.forEach((k,v) -> System.out.println(k+": "+v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "17")
        void javaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void solo64() {
        }
    }

    @Nested
    class EnviromentVariableTest{
        @Test
        void imprimirEnv() {
            Map<String,String> getEnv = System.getenv();
            getEnv.forEach((k,v) -> System.out.println(k+": "+v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void procesadores() {
        }
    }



    @Tag("params")
    @Nested
    class pruebasParametrizadas{

        @DisplayName("Probando debito cuenta repetir")
        @RepeatedTest(value=5, name = "{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
        void debitoCuentaRepetir(RepetitionInfo info) {
            if (info.getCurrentRepetition() == 3) {
                System.out.println("Repeticion actual: "+info.getCurrentRepetition());
            }
            cuenta = new Cuenta("Javy", new BigDecimal("1000.302"));
            cuenta.debito(new BigDecimal("100"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900,cuenta.getSaldo().intValue());
            assertEquals("900.302",cuenta.getSaldo().toPlainString());
        }

        @DisplayName("Probando test parametrizado")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0}")
        @ValueSource(strings = {"10","20","30","50","70","100"})
        void debitoCuentaParametrizadoString(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        }

        @DisplayName("Probando test parametrizado CSV")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {1}")
        @CsvSource({"1,10","2,20","3,30","4,50","5,150.69",})
        void debitoCuentaParametrizadoCsv(String index, String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Probando test parametrizado CSV")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {1}")
        @CsvSource({"20,10","25,20","190,200","25,25","150.69,150.69",})
        void debitoCuentaParametrizadoCsv2(String saldo, String monto) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Probando test parametrizado CSV File")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0}")
        @CsvFileSource(resources = "/data.csv")
        void debitoCuentaParametrizadoCsvFile(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        }


        @DisplayName("Probando test parametrizado CSV Method")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0}")
        @MethodSource("montoList")
        void debitoCuentaParametrizadoCsvMethod(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        static List<String> montoList(){
            return Arrays.asList("10","20","30","50","70","100");
        }
    }


    @Test
    @Timeout(5)
    void pruebaTimeOut() {
        //TimeUnit.SECONDS.sleep(6L);
    }
}