# Proyecto Semestral: Final Reality Tactics

(Tarea 1)

**Curso:** CC3002 - Metodologías de Diseño y Programación
**Autor:** Maximiliano Miranda

----

Este proyecto corresponde al programa simplificado para un juego de rol táctico por turnos inspirado en *Final Fantasy Tactics*, aplicando principios de Programación Orientada a Objetos (POO) y Desarrollo Guiado por Pruebas (TDD).

**Nota sobre el alcance de la entrega:**
Asumiendo completamente la responsabilidad y sabiendo que había sido desaconsejado insistentemente, cometí el desacierto de comenzar muy tarde el desarrollo del proyecto. Por lo que para evitar descuidar el diseño trabajando apresuradamente, esta entrega comprende la implementación completa, y 100% testeada tan solo de lo pedido en la Entrega Parcial 1. Asumiendo que esta tarea es acumulativa y que está directamente relacionada al resto de tareas que quedan del semestre, me comprometo firmemente a ponerme al día e integrar las funcionalidades restantes en el próximo avance del proyecto.


## Arquitectura y Decisiones de Diseño

**Uso de traits como contratos puros:**
Para seguir las reglas del curso, usamos los traits únicamente para definir qué deben hacer las distintas partes del juego, sin guardar variables ni escribir la lógica todavía

**Clases abstractas para no repetir código:**
Para no copiar y pegar el mismo código en cada personaje o arma, creamos clases abstractas intermedias que guardan las partes comunes:
- AbstractCharacter y AbstractMagicCharacter: Tienen la lógica de la vida (currentHp), maná (currentMp) e inventario. Así, clases como Knight o BlackMage solo heredan esto y no tienen que reescribirlo.
- AbstractWeapon y AbstractPotion: Guardan los datos comunes de las armas (puntos de ataque, peso, dueño) y pociones.

**Protección de los datos:**
- Las variables que cambian (como la vida actual o el dueño de un arma) se guardaron como privadas para que no se puedan modificar directamente desde fuera.
- Al pedir el inventario o la lista de unidades de un jugador, entregamos una copia inmutable (List). Así evitamos que alguien modifique la lista original por error.
- No usamos isInstanceOf en ninguna parte del código, respetando el diseño polimórfico del curso.