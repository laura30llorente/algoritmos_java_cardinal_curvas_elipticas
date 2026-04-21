# Implementación en Java de cuatro algoritmos para calcular el número de puntos de curvas elípticas definidas sobre cuerpos primos

Este repositorio contiene el código desarrollado como parte de mi Trabajo de Fin de Grado (TFG) de Matemáticas titulado **«Estudio y comparación de algoritmos para calcular el cardinal de curvas elípticas definidas sobre cuerpos primos»**. 

Todo el código se ha implementado nativamente en el lenguaje **Java**, prescindiendo del uso de librerías externas para facilitar el análisis del comportamiento y flujo completo de los algoritmos.

El objetivo principal del presente TFG es estudiar, analizar, implementar y comparar cuatro algoritmos distintos utilizados para el cálculo del número de puntos de curvas elípticas definidas sobre cuerpos primos $\mathbb{F}_p$, representadas mediante la ecuación de Weierstrass $y^2 \equiv x^3 + ax + b \pmod p$.

---

## Algoritmos implementados

El proyecto implementa cuatro algoritmos, ordenados de menor a mayor eficiencia teórica para curvas de gran tamaño:

1. **Conteo exhaustivo de todos los pares de elementos de $\mathbb{F}_p$**: consiste en considerar todos los pares $(x,y) \in \mathbb{F}_p \times \mathbb{F}_p$ y comprobar cuáles satisfacen la ecuación de Weierstrass de la curva.
   
2. **Método con el símbolo de Legendre**: este método resulta más eficiente debido a que evita iterar sobre los posibles valores de $y$. En este caso, para cada valor de $x\in\mathbb{F}_p$, se comprueba si el término $x^3 + Ax + B$ es un residuo cuadrático en $\mathbb{F}_p$.
   
3. **Escaneo del rango**: consiste en utilizar la cota de Hasse $H(p) = [p+1-2\sqrt{p}, \ \ p+1+2\sqrt{p}]$, con la finalidad de acotar el orden del grupo $E(\mathbb{F}_p)$ en el intervalo.

4. **Algoritmo de Schoof**: algoritmo en tiempo polinomial, que utiliza para su desarrollo conceptos como el endomorfismo de Frobenius $\pi_p$, los polinomios de división $\psi_n$, el Teorema de Hasse y el Teorema Chino del Resto.

---

## Modos de funcionamiento

La aplicación está diseñada con dos módulos principales de ejecución:

### 1. Cálculo del número de puntos (`ConteoPuntosMain.java`) 

En este modo, el usuario debe introducir los parámetros de una curva elíptica (coeficiente $a$, coeficiente $b$ y módulo $p$). Posteriormente, el programa imprime por consola los cuatro algoritmos de conteo de puntos disponibles. 

Una vez que el usuario selecciona una opción, se ejecuta la implementación del algoritmo elegido y, tras su finalización, se muestra un resumen final que contiene información sobre el método usado, el número de puntos de la curva y cuántos segundos y milisegundos ha tardado en calcularlo.

<p align="center">
  <img width="600" height="538" alt="image" src="https://github.com/user-attachments/assets/526a001a-daf3-4652-89d6-7f96a5bbe599" />
</p> 

**Nota:** en los algoritmos del conteo exhaustivo y del símbolo de Legendre, es posible imprimir por consola los primeros puntos de la curva encontrados. Para habilitar esta opción, basta con cambiar el valor de la variable `MODO_DEBUG` a `true` al comienzo de los respectivos archivos (`ConteoExhaustivoPuntos.java` y `MetodoLegendre.java`).

### 2. Análisis de rendimiento y estimación de tiempos (`PruebasMain.java`) 

Debido al crecimiento exponencial en la complejidad temporal de los tres primeros algoritmos, se ha diseñado este modo con clases de estimación para cada uno de ellos. La finalidad es poder determinar aproximadamente cuánto tardarían dichos métodos en calcular el número de puntos para curvas de mayor tamaño.

La fase de medición ejecuta el algoritmo real durante $90$ segundos, lo que garantiza que el tiempo por iteración medido es el del método implementado. A partir de este momento, la extrapolación difiere según el algoritmo. 

<p align="center">
  <img width="700" height="486" alt="image" src="https://github.com/user-attachments/assets/77a393e3-a573-4c21-bb81-3c74019b541b" />
</p>

--- 

## Algoritmo de Schoof

Para la implementación del algoritmo de Schoof, se han empleado los **polinomios univariantes** $\bar{f}_k$ [^1], los cuales dependen exclusivamente de $x$. Esta decisión tiene como finalidad permitir el uso de las funciones de la clase `Polinomio` sobre los polinomios de división sin la interferencia de la variable $y$, apoyándose en la equivalencia $\bar{f}_k = \psi_k$ para índices $k$ impares. El desarrollo de este enfoque se encuentra en el archivo `PolinomioDivision.java`.

Asimismo, dado que el algoritmo de Schoof trabaja con endomorfismos que se representan de la forma $(a(x),\ b(x) \ y)$, se optó por emplear **extensiones algebraicas** en las coordenadas de los endomorfismos para evitar problemas con la variable $y$ en las operaciones. Debido a esto, las operaciones de suma de puntos varían respecto a las de coordenadas afines. Por lo tanto, en la clase `OperacionesPuntoPolinomico.java` se definen las nuevas fórmulas [^2], así como la lógica de decisión para determinar la relación matemática entre los puntos $P$ y $Q$ [^3].

---

## Tecnologías empleadas

<p align="center">
  <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/eclipse-%232C2255.svg?style=for-the-badge&logo=eclipseide&logoColor=white" alt="Eclipse">
</p>

* **Lenguaje de programación:** Java (JDK 26).
* **Entorno de desarrollo:** Eclipse IDE (versión 4.39.0).

---

## Cómo ejecutar el proyecto

1. Clonar este repositorio en una máquina local usando la terminal:
   ```bash
   git clone https://github.com/laura30llorente/algoritmos_java_cardinal_curvas_elipticas.git
   ```
   *Alternativamente, se puede descargar el código en formato ZIP desde el botón «Code» de GitHub.*

2. Abrir un IDE, por ejemplo, **Eclipse IDE**.
   
3. Importar el proyecto en el IDE deseado.
     
4. Ejecución de la aplicación:
   * Dentro de la carpeta `main`, el proyecto dispone de dos archivos ejecutables según el modo seleccionado.
   * Localizar la clase correspondiente al modo que se desee probar y ejecutar como **Java Application**.

---

### Referencias

[^1]:Blake, I. F., Seroussi, G., & Smart, N. P. (1999). *Elliptic Curves in Cryptography*. Cambridge University Press.

[^2]:Cohen, H., Miyaji, A., & Ono, T. (1998). Efficient Elliptic Curve Exponentiation Using Mixed Coordinates. En *Advances in Cryptology — ASIACRYPT '98* (pp. 51-65, Vol. 1514). Springer. https://doi.org/10.1007/3-540-49649-1_6

[^3]:Hankerson, D., Menezes, A., & Vanstone, S. (2004). *Guide to Elliptic Curve Cryptography*. Springer.
