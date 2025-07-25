# SongQL 🎵

A Java-based music database management system that provides a console interface for managing songs, artists, albums, and genres. Built with PostgreSQL and Maven.
Academic project done for Database class final evaluation.

## Features

### Core Functionality
- **Music Management**: Add, update, and remove songs from the database
- **Consultation System**: View all songs with customizable sorting options
- **Playlist Generation**: Create temporary playlists by genre with random song selection
- **Comprehensive Data Model**: Support for songs, artists, albums, genres, and tracks

### Menu Options
1. **Consult Songs** - View all songs with sorting options (by title, release date, author, album)
2. **Add Song** - Add new songs with optional album and track information
3. **Update Song Title** - Modify existing song titles
4. **Remove Song** - Delete songs and associated records
5. **Create Playlist** - Generate random playlists by genre
6. **Exit** - Close the application

## Database Schema

The application manages the following entities:
- **Songs** (`musica`) - Title, release date, author
- **Artists** (`autor`) - Artist names
- **Albums** (`album`) - Album names
- **Genres** (`genero`) - Music genres
- **Tracks** (`faixa`) - Track numbers within albums
- **Song-Genre Relationships** (`musica_genero`) - Many-to-many relationship

## Technologies Used

- **Java 21** - Programming language with preview features enabled
- **PostgreSQL** - Database management system
- **JDBC** - Database connectivity
- **Maven** - Build and dependency management
- **JUnit 4.11** - Testing framework

## Prerequisites

- Java 21 or higher
- PostgreSQL database server
- Maven 3.6+
- PostgreSQL JDBC driver (included in dependencies)

## Database Configuration

The application connects to PostgreSQL with the following default settings:
- **URL**: `jdbc:postgresql://localhost:5432/postgres`
- **Username**: `postgres`
- **Password**: `postgres`

Make sure your PostgreSQL server is running and accessible with these credentials, or modify the connection parameters in the `App.java` file.

## Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/joaopedroseq/SongQL.git
   cd SongQL
   ```

2. **Compile the project**:
   ```bash
   mvn compile
   ```

3. **Run the application**:
   ```bash
   mvn exec:java -Dexec.mainClass="aor.App"
   ```

   Or build and run the JAR:
   ```bash
   mvn package
   java -jar target/jdbc-tutorial.jar
   ```

## Usage Examples

### Adding a Song
1. Select option 2 from the main menu
2. Enter song title, release date (yyyy-MM-dd format), author, and genre
3. Optionally specify album name and track number
4. The system automatically creates artists, albums, and genres if they don't exist

### Creating a Playlist
1. Select option 5 from the main menu
2. Choose a genre from the displayed list
3. Specify the number of songs for the playlist
4. View the randomly generated playlist

### Consulting Songs
1. Select option 1 from the main menu
2. Choose sorting criteria (title, release date, author, album, or default by ID)
3. Select ascending or descending order
4. View the formatted song list

## Project Structure

```
src/
├── main/
│   └── java/
│       └── aor/
│           ├── App.java              # Main application class
│           └── ValidacaoInput.java   # Input validation utilities
└── test/
    └── java/                         # Test files
```

## Key Features

### Data Validation
- Input validation for all user entries
- Date format validation (yyyy-MM-dd)
- Numeric validation for track numbers and IDs
- Special character handling and cleanup

### Database Operations
- **CRUD Operations**: Complete Create, Read, Update, Delete functionality
- **Relationship Management**: Automatic handling of foreign key relationships
- **Data Integrity**: Prevents duplicate tracks in albums
- **Cleanup Operations**: Removes empty albums when songs are deleted

### User Interface
- ASCII art logo on startup
- Formatted table output for song listings
- Interactive menu system with input validation
- Clear error messages and success confirmations

## SQL Operations Examples

The application performs various SQL operations including:

```sql
-- Query songs with album information
SELECT identificador, titulo, data_criacao, autor_nome, 
       coalesce(album_nome, 's/album') as album,
       coalesce(faixa.num_faixa::text, '') as faixa
FROM musica
LEFT OUTER JOIN faixa ON musica.identificador = faixa.musica_identificador;

-- Generate random playlist by genre
CREATE TEMP TABLE temp_playlist AS
SELECT musica.identificador, musica.titulo, musica.data_criacao, 
       musica.autor_nome, genero.nome AS genero_nome
FROM musica
INNER JOIN musica_genero ON musica.identificador = musica_genero.musica_identificador
INNER JOIN genero ON musica_genero.genero_nome = genero.nome
WHERE LOWER(genero.nome) = ?
ORDER BY RANDOM()
LIMIT ?;

-- Update song title
UPDATE musica
SET titulo = ?
WHERE identificador = ?;

-- Remove song and associated records
DELETE FROM musica_genero WHERE musica_identificador = ?;
DELETE FROM faixa WHERE musica_identificador = ?;
DELETE FROM musica WHERE identificador = ?;
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is available for educational and personal use.

## Authors

- **João Pedro** - Initial development and database design
- **Colleague** - Collaborative development (original version)

---

*SongQL - Your personal music database management solution* 🎶




