# InventorySystem (Modeling and Simulation Final Project)

A Java Swing desktop application that bundles four classic discrete simulation exercises
from a modeling and simulation course: a single/multi server queue, an (M, N) inventory
system, a parts vendor (newsvendor) problem and an event-list queue simulation.

---

## Table of Contents

1. [Overview](#overview)
2. [Project Status](#project-status)
3. [How It Works](#how-it-works)
4. [Simulations](#simulations)
5. [Technology Stack](#technology-stack)
6. [Project Structure](#project-structure)
7. [Getting Started](#getting-started)
8. [Configuration](#configuration)
9. [Screens](#screens)
10. [Data Model](#data-model)
11. [Testing](#testing)
12. [Known Issues](#known-issues)
13. [License](#license)

---

## Overview

### Purpose

Many simulation exercises are solved by hand with a probability table, a list of random
numbers and a long results table. This project automates that work. The user enters the
input values and the probability distributions in dialog boxes, and the program builds the
probability tables, draws the random numbers, runs the simulation and shows the full
results table on screen.

Although the repository is named `InventorySystem`, it contains more than an inventory
model. The NetBeans project itself is called `final modeling`, and the code lives in the
package `pkgfinal.modeling`.

### Intended users

- Students of a modeling and simulation course who want to check hand calculations.
- Teachers who want to show how random numbers are mapped to values through cumulative
  probability ranges.

### Core capabilities

- A main menu window to choose one of four simulations.
- Input of all parameters through simple dialog boxes.
- Three ways to define a probability distribution: random, equal ("same") or entered by
  hand ("different").
- A probability table per distribution with value, probability, cumulative probability and
  random number range (1-100).
- A day-by-day or customer-by-customer results table.
- A summary dialog with totals for the inventory and vendor simulations.

---

## Project Status

This is a finished university course project (single commit, "Final Project", October 2025).
It is not under active development.

- All four simulations are implemented and reachable from the main menu.
- There are no automated tests.
- Compiled `.class` files under `build/` are committed to the repository.
- Some calculation and input-handling details are simplified; see
  [Known Issues](#known-issues).

---

## How It Works

```
+------------------------+
| FinalModeling.main()   |
+-----------+------------+
            |
            v
+------------------------+       +-------------------------------+
| CombinedSimulationGUI  |       | JOptionPane input dialogs     |
| (menu with 5 buttons)  |------>| (parameters, distributions)   |
+-----------+------------+       +---------------+---------------+
            |                                    |
            v                                    v
+------------------------------------------------------------------+
| One of: SingleMultiServerSimulation | MNInventorySimulation |    |
|         NewsVendorSimulation        | EventSimulation            |
+-----------------------------+------------------------------------+
                              |
                              v
+------------------------------------------------------------------+
| JTable windows: probability tables + results table               |
| JOptionPane: summary of totals (inventory and vendor only)       |
+------------------------------------------------------------------+
```

1. `FinalModeling.main()` starts the application and opens `CombinedSimulationGUI`.
2. The menu window shows the buttons "Single/Multi Server Simulation",
   "M/N Inventory Simulation", "Parts Vendor Simulation", "Event Simulation" and "Exit".
3. The chosen simulation asks for its parameters in a series of input dialogs.
4. For each random variable, the user picks how probabilities are created (random, same or
   different). The program then builds a cumulative probability table and maps it to the
   random number range 1-100.
5. The probability tables are shown in their own windows.
6. The simulation draws a random number for each step, looks up the matching value in the
   table and computes the step.
7. The results are shown in a scrollable table window. The inventory and vendor simulations
   also show a summary dialog.

Pressing Cancel in most input dialogs shows "Process cancelled. Exiting..." and closes the
program.

---

## Simulations

### 1. Single/Multi Server Simulation

File: `src/pkgfinal/modeling/SingleMultiServerSimulation.java`

A queue with one server or two servers ("machines").

- Inputs: server type (`S` or `M`), number of customers, inter-arrival time (IAT) values
  and their probabilities, Service Time 1 values and probabilities, and for the multi
  server case also Service Time 2 values and probabilities.
- Logic: each customer arrives after a random IAT. In single server mode every customer is
  served by machine 1. In multi server mode a customer goes to machine 1 if it is free at
  the same time or earlier than machine 2, otherwise to machine 2.
- Output columns: Customer, Rand IAT, IAT, Arrival, Rand Srv, Service, M1 Start, M1 End,
  M2 Start, M2 End.
- Totals at the end of the table: total waiting time and total idle time of each machine.

### 2. M/N Inventory Simulation

File: `src/pkgfinal/modeling/MNInventorySimulation.java`

A periodic inventory system with a reorder point and a fixed order quantity.

- Inputs: starting inventory, order quantity, order condition (reorder level), number of
  weeks, minimum and maximum demand, minimum and maximum lead time, and the probabilities
  for each demand and lead time value.
- Logic: the simulation runs `weeks x 7` days, grouped into 7-day cycles. Each day a random
  demand is drawn. Unmet demand is recorded as a shortage and is backordered. When the end
  inventory is at or below the order condition and no order is open, an order is placed
  with a random lead time. When the order arrives, it first clears the open shortage.
- Output columns: Cycle, Day, Begin Inventory, Random Demand, Demand, End Inventory,
  Shortage, Order Placed, Order Quantity, Random Lead Time, Days Until Order Arrive.
- Summary: total shortage, total orders placed and the sum of daily end inventory.

### 3. Parts Vendor Simulation (newsvendor problem)

File: `src/pkgfinal/modeling/NewsVendorSimulation.java`

A newsvendor-style problem where a fixed number of parts is bought every day and unsold
parts are scrapped.

- Inputs: cost to buy a part, selling price, scrap value per unsold part (all in cents),
  number of days, number of parts bought per day, the probability choice for the part type
  table (`same`, `different` or `random`), number of demand levels, minimum demand value and
  the probability choice for the demand table.
- Part types: `Good`, `Fair` and `Poor`.
- Demand levels: start at the minimum demand value and increase in steps of 10.
- Logic per day: draw a random part type, draw a random demand, then compute revenue, cost,
  lost profit (when demand is higher than the purchase), salvage (when demand is lower) and
  daily profit.
- Output columns: Day, Purchased Part, Random PartType, PartType, Random Demand, Demand,
  Revenue, Lost Profit, Salvage, Profit.
- Summary: total revenue, total cost, total lost profit, total salvage and total profit.

### 4. Event Simulation

File: `src/pkgfinal/modeling/EventSimulation.java`

A single server queue shown as an event list (arrivals and departures).

- Inputs: number of customers, IAT values and probabilities, service time values and
  probabilities.
- Logic: the program generates IAT and service times, computes arrival times, creates an
  arrival event (`A`) for each customer and a departure event (`D`) at the end of each
  service, and sorts all events by time.
- Output: one window split in two tables.
  - IAT table: Customer, IAT, Arrival Time.
  - Event table: Type, Clock, LQ (customers in queue), LS (customer in service, 0 or 1) and
    the current Checkout Line.

---

## Technology Stack

| Area            | Technology                                      |
|-----------------|-------------------------------------------------|
| Language        | Java (source and target level 17)               |
| User interface  | Java Swing (`JFrame`, `JTable`, `JOptionPane`)  |
| Build           | Apache Ant through the NetBeans project files   |
| IDE             | Apache NetBeans (`nbproject/`)                  |
| Dependencies    | None beyond the Java standard library           |

---

## Project Structure

```
InventorySystem/
|-- build.xml                      Ant build file (NetBeans default)
|-- manifest.mf                    JAR manifest template
|-- build/
|   `-- classes/pkgfinal/modeling/ Compiled .class files (committed)
|-- nbproject/
|   |-- build-impl.xml             Generated NetBeans build script
|   |-- genfiles.properties
|   |-- project.properties         Main class, Java level, output paths
|   |-- project.xml                Project name "final modeling"
|   `-- private/                   Local IDE settings (committed)
`-- src/
    `-- pkgfinal/modeling/
        |-- FinalModeling.java               Entry point (main class)
        |-- CombinedSimulationGUI.java       Main menu window
        |-- SingleMultiServerSimulation.java Single/two server queue
        |-- MNInventorySimulation.java       (M, N) inventory system
        |-- NewsVendorSimulation.java        Parts vendor (newsvendor) problem
        `-- EventSimulation.java             Event-list queue simulation
```

---

## Getting Started

### Prerequisites

- JDK 17 or newer
- One of:
  - Apache NetBeans, or
  - Apache Ant, or
  - only the JDK (`javac` and `java`)
- A desktop environment, because the program opens Swing windows

### Get the code

```bash
git clone https://github.com/ahmadkobah/InventorySystem.git
cd InventorySystem
```

### Option A: NetBeans

1. Open NetBeans and choose File > Open Project.
2. Select the `InventorySystem` folder.
3. Run the project. The main class is `pkgfinal.modeling.FinalModeling`.

### Option B: Ant

```bash
ant jar
java -jar dist/final_modeling.jar
```

`ant jar` uses the NetBeans build scripts and writes the JAR to `dist/final_modeling.jar`.

### Option C: JDK only

```bash
javac -d out src/pkgfinal/modeling/*.java
java -cp out pkgfinal.modeling.FinalModeling
```

---

## Configuration

There are no configuration files, environment variables or command line options. All
input is entered at run time through dialog boxes.

Build settings are in `nbproject/project.properties`:

- `main.class` - the class that is started (`pkgfinal.modeling.FinalModeling`)
- `javac.source` / `javac.target` - Java language level
- `dist.jar` - path of the built JAR

---

## Screens

| Window                                    | Opened by                            |
|-------------------------------------------|--------------------------------------|
| Combined Simulation Project (main menu)   | Program start                        |
| IAT Table, Service Time 1/2 Table         | Single/Multi Server Simulation       |
| Single/Multi Server Simulation Results    | Single/Multi Server Simulation       |
| Demand Table, Lead Time Table             | M/N Inventory Simulation             |
| M/N Inventory Simulation Results          | M/N Inventory Simulation             |
| Simulation Summary (dialog)               | M/N Inventory and Parts Vendor       |
| PartsType Table, Demand Table             | Parts Vendor Simulation              |
| Simulation Results                        | Parts Vendor Simulation              |
| Event Simulation Results (split view)     | Event Simulation                     |

---

## Data Model

The program keeps all data in memory. Nothing is saved to disk or to a database.

| Class                              | Purpose                                                   |
|------------------------------------|-----------------------------------------------------------|
| `EventSimulation.Customer`         | IAT, arrival time, service time, service begin/end, idle  |
| `EventSimulation.Event`            | Event type (`A` or `D`), time and customer id             |
| `EventSimulation.EventComparator`  | Sorts events by time                                      |
| `NewsVendorSimulation.NewsType`    | Part type (Good/Fair/Poor) with probability and range     |
| `NewsVendorSimulation.Demand`      | Demand level with probability, cumulative and range per part type |
| `NewsVendorSimulation.Simulation`  | Runs the daily vendor simulation and shows the results    |

The server and inventory simulations use plain arrays (`int[]` values and `double[]`
probabilities) and build their result tables as lists of string rows.

---

## Testing

There are no automated tests in the repository. The NetBeans project has no test folder
configured with test sources. Results can be checked by hand by comparing the shown random
numbers and ranges with the probability tables.

---

## Known Issues

These points were found while reading the source code:

- **Random results.** No fixed random seed is used, so runs cannot be repeated with the
  same numbers.
- **Parts Vendor - demand column.** The demand is always looked up in the `Good` ranges,
  even when the drawn part type is `Fair` or `Poor`.
- **Parts Vendor - revenue.** Revenue is `demand x selling price`, also when demand is
  higher than the number of parts bought.
- **Parts Vendor - units.** Prices are entered in cents, but the summary dialog shows the
  totals with a `$` sign.
- **Parts Vendor - "different" demand probabilities.** Probabilities are only rejected when
  a column sums to more than 1. A sum below 1 is accepted.
- **M/N Inventory and Single/Multi Server - "different" probabilities.** A sum below 1 is
  accepted (the server simulation does not check the sum at all). Random numbers above the
  last range then map to the last value.
- **Single/Multi Server.** In multi server mode a Service Time 1 value is drawn and shown in
  the `Service` column for every customer, also for customers served by machine 2.
- **Event Simulation - input.** The dialogs in this simulation do not validate input.
  Pressing Cancel or typing text causes an unhandled exception instead of an error message.
- **Event Simulation - window.** Closing the results window ends the whole program, not
  only that window.
- **Repository content.** Compiled classes in `build/` and the IDE folder
  `nbproject/private/` are committed and are normally excluded with a `.gitignore`.

---

## License

Private. All rights reserved.

Author: Ahmad Kobah
