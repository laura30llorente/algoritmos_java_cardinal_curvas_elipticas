package com.utad.mais.tfgmaco.utilidades;

import java.math.BigInteger;
import com.utad.mais.tfgmaco.curva.*;

public class ImpresionDetalles {
	public static void imprimirInformacionCurva(CurvaElipticaFp curva) {
		System.out.println("\nCurva eliptica definida sobre F_" + curva.getModulo());
		System.out.println("E: y^2 ≡ x^3 + " + curva.getA() + "x + " + curva.getB() + "  (mod " + curva.getModulo() + ")");
		System.out.println("Curva " + (curva.esCurvaNoSingular() ? "NO" : "SI") + " singular");
	}
	
	public static void imprimirInformacionConteo(String nombreMetodo, BigInteger numeroPuntos, long duracion) {
		System.out.println("\nMetodo de conteo: " + nombreMetodo);
		System.out.println("Numero de puntos de la curva (contando el punto en el infinito): " + numeroPuntos);
		System.out.println("Duracion del metodo: " + duracion + " milisegundos, " + (duracion / 1000) + " segundos");
	}
}
