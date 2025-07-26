# ♟️ GamBit Chess Engine V1.0 ♟️

![GamBit Logo](./res/menu/gambitlogo.png)

GamBit is a custom-built chess engine implemented in **Java**, utilizing bitboard-based move generation, **negamax search with alpha-beta pruning**, and **quiescence search** to evaluate complex positions. Inspired by the techniques used in engines like Stockfish and Crafty, GamBit is designed to be compact, efficient, and educational.

---

## 🚀 Features

### ✅ Bitboard Representation
GamBit uses **18 Java `long` values** to efficiently track board state using bitwise operations:

- **White/Black Piece Types**: Tracks individual piece types (e.g., `whitePawns`, `blackKnights`).
- **White/Black Pieces**: Tracks all pieces of each side to calculate blockers and threats.
- **En Passant**: Identifies pawns vulnerable to en passant.
- **HasNotMoved**: Flags for unmoved pieces (castling and 2-step pawn logic).
- **CheckingPieces**: Tracks which pieces are checking the king.

### ✅ Negamax with Alpha-Beta Pruning
Uses recursive **negamax** with **alpha-beta pruning** to simulate potential moves and cut unpromising branches—greatly improving speed and depth.

### ✅ Quiescence Search
Avoids evaluation in “noisy” tactical positions by extending the search only with captures and checks—reducing blunders in volatile states.

### ✅ MVV_LVA Move Ordering
Prioritizes moves that capture high-value pieces with low-value attackers, improving pruning performance.

---

## 🧠 Engine Benchmarking

### ✅ Best Depth
- Effective play up to **depth 5** before timing becomes significant.

### ✅ NPS Benchmark Results

<pre> ``` ========== Benchmark Result ========== Depth: 5 | Runs: 10 Total Nodes: 7,727,500 Total Time: 6.83 sec Average Time/Run: 0.683 sec Average Nodes/Run: 772,750 Average NPS: 1,131,924 nodes/sec ``` </pre>

### ✅ Engine Evaluation

- Evaluated by **Stockfish 16** at an estimated **2000–2250 ELO**
- Defeated **Komodo engines** rated 2000–2200 on Chess.com

---

## 🧪 How to Run

### 🔹 Play Against GamBit
- Open `src/view/Main.java`
- Run as a Java application to launch the game interface

### 🔹 Run Benchmark Test
- Open `src/model/Main.java`
- Run as Java application to start NPS test

---

## 🖥️ UI Preview

![GamBit Menu Screenshot](./res/menu/menu-screenshot.png)

---

## 🔧 Configuration

| Parameter     | Description                                             |
|---------------|---------------------------------------------------------|
| `depth`       | Search depth for minimax search                         |
| `MATE_SCORE`  | Evaluation constant for checkmate detection             |
| `prevMove1/2` | Used to avoid two-move repetition during root searches  |

---

## 🛠️ Planned Updates

- ♻️ **Zobrist Hashing** for repetition detection and transposition table support  
- 🧮 **Positional Evaluation** (king safety, pawn structure, center control)  
- 📊 **Move History Visualization**  
- 💡 **Multithreading** for parallel root move search  
- ⚙️ **Customizable GUI**: evaluation bar, undo button, side selector  

---

## 📁 Project Structure

<pre> ``` /src ├── /model │ ├── GamBit.java ← Core engine logic │ ├── Bitboard.java ← Bitboard representation & move generation │ └── MoveSet.java ← Move data container └── /view ├── BoardPanel.java ← GUI rendering & game loop └── Main.java ← GUI entry point /res ├── /magic_data ← Magic bitboard lookup tables ├── /menu ← GUI resource images └── /piece ← Chess piece images ``` </pre>

---

## 🙏 Acknowledgments

- Inspired by **Stockfish**, **Crafty**, and the broader chess programming community
- Special thanks to [chessprogramming.org](https://www.chessprogramming.org) for bitboard concepts and search optimization techniques

---

## 📜 License

This project is open for **educational and personal use**.

To redistribute or contribute, consider adding a license such as [MIT](https://opensource.org/licenses/MIT) or [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0).

---

**Enjoy mastering the board with GamBit!**
