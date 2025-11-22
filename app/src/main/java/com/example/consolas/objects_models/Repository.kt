package com.example.consolas.objects_models

import com.example.consolas.models.Console

object Repository {
    val listConsoles: List<Console> = listOf(
        Console(
            "Nintendo Entertainment System (NES)",
            "1983",
            "Nintendo",
            "Una consola de 8 bits que revitalizó la industria del videojuego y se volvió un ícono mundial.",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/NES-Console-Set.png/2560px-NES-Console-Set.png"
        ),
        Console(
            "Super Nintendo Entertainment System (SNES)",
            "1990",
            "Nintendo",
            "Consola de 16 bits famosa por sus títulos legendarios y avances en sonido y gráficos.",
            "https://upload.wikimedia.org/wikipedia/commons/3/3e/SNES-Mod1-Console-Set.jpg"
        ),
        Console(
            "Nintendo 64",
            "1996",
            "Nintendo",
            "Primera consola de Nintendo con gráficos en 3D reales y soporte para 4 jugadores.",
            "https://upload.wikimedia.org/wikipedia/commons/8/8c/N64-Console-Set.jpg"
        ),
        Console(
            "PlayStation 1",
            "1994",
            "Sony",
            "Consola de 32 bits que lanzó a Sony al mercado con gran éxito mundial.",
            "https://upload.wikimedia.org/wikipedia/commons/8/82/PlayStation-SCPH-1000-with-Controller.jpg"
        ),
        Console(
            "PlayStation 2",
            "2000",
            "Sony",
            "La consola más vendida de la historia, con un enorme catálogo de juegos.",
            "https://upload.wikimedia.org/wikipedia/commons/4/4e/Ps2-fat-console.jpg"
        ),
        Console(
            "PlayStation 3",
            "2006",
            "Sony",
            "Primera consola de Sony con soporte HD y Blu-ray, revolucionando el multimedia.",
            "https://upload.wikimedia.org/wikipedia/commons/5/59/PS3-FatConsole.png"
        ),
        Console(
            "Xbox",
            "2001",
            "Microsoft",
            "La primera consola de Microsoft, pionera con Xbox Live y hardware potente para su época.",
            "https://upload.wikimedia.org/wikipedia/commons/4/43/Xbox-console.jpg"
        ),
        Console(
            "Xbox 360",
            "2005",
            "Microsoft",
            "Consola muy popular que consolidó el juego online y trajo grandes franquicias.",
            "https://upload.wikimedia.org/wikipedia/commons/0/03/Xbox-360-Pro-wController.jpg"
        ),
        Console(
            "Sega Mega Drive",
            "1988",
            "Sega",
            "Consola de 16 bits conocida por Sonic y por la guerra de consolas con Nintendo.",
            "https://upload.wikimedia.org/wikipedia/commons/3/36/Sega-Mega-Drive-JP-Mk1-Console-Set.jpg"
        ),
        Console(
            "GameCube",
            "2001",
            "Nintendo",
            "Consola compacta con discos mini-DVD y un catálogo muy querido por fans.",
            "https://upload.wikimedia.org/wikipedia/commons/0/0d/Gamecube-console.jpg"
        )
    )
}
