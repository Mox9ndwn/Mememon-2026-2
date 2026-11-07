# Proyecto Semestral: Final Reality Tactics


**Curso:** CC3002 - Metodologías de Diseño y Programación
**Autor:** Maximiliano Miranda


Este proyecto corresponde al programa simplificado para un juego de rol táctico por turnos inspirado en *Final Fantasy Tactics*, aplicando principios de Programación Orientada a Objetos (POO) y Desarrollo Guiado por Pruebas (TDD).


------


## Tarea 1: Estructura Base, Mapa y Sistema de Turnos


En esta etapa inicial el foco estuvo en modelar las entidades básicas del combate y la infraestructura del juego:

### 1. Personajes, Enemigos y Armas

**Uso de traits como contratos puros:**
Para seguir las reglas del curso, usamos los traits como contratos puros para definir qué deben hacer las distintas entidades, sin guardar variables mutables.

**Clases abstractas para no repetir código:**
Para no copiar y pegar el mismo código en cada personaje o arma, creamos clases abstractas intermedias que guardan las partes comunes:
- AbstractCharacter y AbstractMagicCharacter: Tienen la lógica de la vida (currentHp), maná (currentMp), ranura de arma e inventario. Así, clases como Knight o BlackMage solo heredan esto y no tienen que reescribirlo.
- AbstractWeapon y AbstractPotion: Guardan los datos comunes de las armas (puntos de ataque, peso, dueño) y pociones.

**Protección de los datos:**
- Las variables que cambian (como la vida actual o el dueño de un arma) se guardaron como privadas para que no se puedan modificar directamente desde fuera.
- Al pedir el inventario o la lista de unidades de un jugador, entregamos una copia inmutable (List). Así evitamos que alguien modifique la lista original por error.

### 2. Mapa y Turnos (Puesta al día)

Para completar la base de la primera tarea, se implementaron:

**Paneles del mapa (Panel):**
Cada casilla conoce sus coordenadas (x, y), las unidades presentes en ella y sus casillas vecinas (neighbors), conectadas de forma bidireccional.

**Programador de turnos (TurnScheduler):**
Lleva el conteo de las barras de acción de cada unidad. Cuando una unidad acumula puntos suficientes según su peso (weight), está lista para actuar. Maneja el reinicio de barras al actuar y la resolución de empates según el orden de llegada.



------



## Tarea 2: Interacciones, Double Dispatch, Restricciones y Controlador


En esta segunda tarea el objetivo principal fue conectar todo lo creado. Que las unidades interactúen entre sí, se muevan por el mapa, respeten las reglas del juego mediante excepciones descriptivas y todo sea coordinado por un controlador central.

### 1. Interacción de entidades (Double Dispatch)

El desafío fue cómo hacer que una unidad ejecute una acción sobre otra sin usar isInstanceOf, condicionales por tipo (if/else) o casteos, respetando el diseño polimórfico del curso.

Para resolverlo, utilizamos el patrón Double Dispatch:
1. La unidad que actúa (Source) llama a doAction(action, target).
2. La acción (Action) se ejecuta sabiendo exactamente qué tipo de acción es (Attack, Move, Heal, Purify, etc.) y llama a un método específico en el objetivo (Target). Por ejemplo, Attack llama a target.receiveAttack(damage), Move llama a target.receiveMove(unit), Heal llama a target.receiveHeal(amount), etc.
3. El objetivo (Target) reacciona según su propia clase:
    * Si un Knight recibe un ataque, descuenta su defensa del daño recibido.
    * Si un Panel recibe un movimiento, incorpora a la unidad en su casilla.
    * Si un objetivo no es válido para esa acción (por ejemplo, intentar curar a un enemigo o purificar a un aliado), salta una excepción explicativa.

### 2. Restricciones de Equipamiento (Double Dispatch en Armas)

El enunciado pedía que cada clase de personaje solo pudiera equiparse ciertas armas específicas. Para resolver esto limpiamente y sin preguntar por tipos:
* Al hacer character.equip(weapon), el arma delega con weapon.equipTo(character).
* Cada arma concreta llama al método correspondiente en el personaje.
* En la clase base AbstractCharacter, todos estos métodos lanzan una excepción de incompatibilidad por defecto. Luego, cada personaje concreto (como Knight o BlackMage) solo sobreescribe los métodos de las armas que sí tiene permitidas. Si intenta ponerse un arma incompatible, automáticamente salta el error por defecto.

### 3. Manejo de Errores y Casos de Borde

Creamos una jerarquía clara de excepciones bajo una clase base GameRuleException para que cuando algo falle, el mensaje explique exactamente el motivo:

**Unidad derrotada:** Una unidad con 0 o menos HP no puede realizar acciones (DefeatedUnitException) ni tampoco puede ser objetivo de ataques o curaciones (InvalidTargetException).
**Arma necesaria para atacar:** Un personaje no puede atacar si tiene las manos vacías (MissingWeaponException).
**Hechizos mágicos:** Castear magia exige tener maná suficiente (InsufficientManaException) y tener equipada un arma mágica (Wand o Staff).
**Hechizos dirigidos:** Heal solo puede recibirse en personajes aliados y Purify únicamente en enemigos (InvalidTargetException).
**Movimiento en el mapa:** Una unidad solo puede moverse a casillas que se encuentren en la lista de casillas vecinas (neighbors) de su posición actual (InvalidMovementException).

### 4. Orquestación con GameController

El GameController funciona como el director del juego.

* No contiene lógica de combate directa, sino que se apoya en el TurnScheduler para saber a quién le toca jugar y avanzar el reloj de turnos.
* Conoce las acciones disponibles de cada unidad.
* Al ejecutar una acción, encapsula la llamada dentro de un manejo seguro. Es decir que si se viola alguna regla del juego, atrapa la excepción GameRuleException, guarda el mensaje de error para que la aplicación o la interfaz puedan informarlo, y evita que el programa se caiga de golpe.
* Permite reiniciar la partida con resetGame().

#### Decisiones funcionales:

**Daño no negativo:** Si la defensa de una unidad es mayor que el ataque recibido, el daño efectivo se ajusta a 0 en lugar de curar a la unidad.
**Topes de vida y maná:** Al recibir curaciones o cambios de estadísticas, la vida y el maná nunca pueden sobrepasar el máximo de la unidad ni bajar de 0.
**Actualización de posición al moverse:** Al moverse a un nuevo panel vecino, la unidad se desvincula automáticamente del panel anterior para mantener la coherencia del mapa.