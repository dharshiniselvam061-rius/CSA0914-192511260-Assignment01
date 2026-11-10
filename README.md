# CSA09-<RegisterNumber>-Assignment1 – Campus Smart-Parking System

|||
|-|-|
|**Name**|S Dharshini|
|**Register number**|192511260|
|**Java version**|OpenJDK 21 (`javac -version` → 21.0.x). Java 16+ is enough (`instanceof` patterns).|
|**Report**|`REPORT.md` / `REPORT.docx` (Tasks 1–12: analysis, pseudocode, UML, test tables, complexity analyses)|

## How to compile and run

Everything at once (compiles every task and rewrites the `output\*.txt` files):

```bash
./run\_all.sh
```

Or one task at a time (run from the repository root):

|Task|Compile|Run|Result|
|-|-|-|-|
|1|`javac -d build/T1 Task01/\*.java`|`java -cp build/T1 Task01Demo`|Search time of objects vs parallel arrays – both grow linearly|
|2|`javac -d build/T2 Task02/\*.java`|`java -cp build/T2 Task02Demo 130 30` (or run without arguments and type `130 30`)|Integer-division bug shown (Rs 60 instead of Rs 90); jagged vs 1-D vs `ArrayList` slot storage|
|3|`javac -d build/T3 Task03/\*.java`|`java -cp build/T3 ParkingFacility` · `FeeSlabs` · `AltFacility`|**Complete the Code** prints the expected output exactly; fee slabs; alternative allocator measured|
|4|`javac -d build/T4 Task04/\*.java`|`java -cp build/T4 Task04Demo`|Constructor chaining trace; constructor vs setters vs factory|
|5|`javac -d build/T5 Task05/\*.java`|`java -cp build/T5 Task05Demo`|static counter / static block / object passing / garbage-collection eligibility|
|6, 8, 9|`javac -d build/T6 Task06\_09\_FeeDemo/\*.java`|`java -cp build/T6 FeeDemo` · `ConstructorTrace`|**Complete the Code** prints the expected output (`30.0`, `90.0`, `210.0`, `true`)|
|7|`javac -d build/T7 Task07/\*.java`|`java -cp build/T7 OverrideDemo`|`@Override`, `final`, `equals/hashCode/toString`|
|8|`javac -d build/T8 Task08/\*.java`|`java -cp build/T8 PolymorphismDemo`|Overloading vs overriding; dispatch vs `instanceof` vs `switch` timing|
|9|`javac -d build/T9 Task09/\*.java`|`java -cp build/T9 InterfaceDemo`|Abstract class vs interface, default and static methods|
|10|`javac -d build/T10 $(find Task10/src -name '\*.java')`|`java -cp build/T10 com.campus.encaps.EncapsulationDemo`|Encapsulation, access modifiers, wrapper/autoboxing risks|
|11|`javac -d build/T11 $(find Task11/src Task11/test Task11/bench -name '\*.java')`|`java -cp build/T11 com.campus.parking.app.ParkingApp < Task11/input/commands.txt` · `com.campus.parking.test.ParkingTests` · `StorageBench`|Full system; **43/43 tests pass**|
|12|`javac -d build/T12 $(find Task12/src Task12/test -name '\*.java')`|`java -cp build/T12 com.campus.parking.test.NewRequirementTests`|New vehicle types, day pass, 5 uneven floors; **39/39 new tests pass**, and the 43 Task 11 tests still pass on the new source|

Each task folder contains its source code, input files (where used), and the saved console output (`output\*.txt`). Add your own screenshots of these runs next to them.

## Notes

* The PDF headings for Task 4 and Task 7 are garbled/missing; they were inferred from the text. The first lines of `main()` in the two "Complete the Code" programs are reconstructed (only the end is visible in the PDF).
* `Task12/src` starts as a copy of `Task11/src`; `diff -rq Task11/src Task12/src` shows exactly what changed (4 modified files, 2 new).

