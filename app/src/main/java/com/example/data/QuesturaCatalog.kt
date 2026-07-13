package com.example.data

object QuesturaCatalog {
    private data class RawProvince(
        val capital: String,
        val code: String,
        val region: String,
        val address: String,
        val phone: String
    )

    private val rawProvinces = listOf(
        RawProvince("L'Aquila", "AQ", "Abruzzo", "Via Strinella, 1, 67100 L'Aquila AQ", "+39 0862 4301"),
        RawProvince("Chieti", "CH", "Abruzzo", "Via Bettino Ricasoli, 1, 66100 Chieti CH", "+39 0871 3421"),
        RawProvince("Pescara", "PE", "Abruzzo", "Via Pesaro, 7, 65121 Pescara PE", "+39 085 2051"),
        RawProvince("Teramo", "TE", "Abruzzo", "Viale Bovio, 31, 64100 Teramo TE", "+39 0861 2591"),
        RawProvince("Matera", "MT", "Basilicata", "Via Giuseppe Gattini, 12, 75100 Matera MT", "+39 0835 3781"),
        RawProvince("Potenza", "PZ", "Basilicata", "Viale dell'Unicorno, 1, 85100 Potenza PZ", "+39 0971 3341"),
        RawProvince("Catanzaro", "CZ", "Calabria", "Piazza Trieste, 1, 88100 Catanzaro CZ", "+39 0961 8891"),
        RawProvince("Cosenza", "CS", "Calabria", "Via Vercillo, 1, 87100 Cosenza CS", "+39 0984 8971"),
        RawProvince("Crotone", "KR", "Calabria", "Via Nuova Poggioreale, 1, 88900 Crotone KR", "+39 0962 9641"),
        RawProvince("Reggio Calabria", "RC", "Calabria", "Corso Garibaldi, 422, 89125 Reggio Calabria RC", "+39 0965 4111"),
        RawProvince("Vibo Valentia", "VV", "Calabria", "Via Emilia, 1, 89900 Vibo Valentia VV", "+39 0963 9621"),
        RawProvince("Avellino", "AV", "Campania", "Via Francesco Saverio, 1, 83100 Avellino AV", "+39 0825 2061"),
        RawProvince("Benevento", "BN", "Campania", "Via Raffaele De Caro, 11, 82100 Benevento BN", "+39 0824 57111"),
        RawProvince("Caserta", "CE", "Campania", "Piazza della Prefettura, 1, 81100 Caserta CE", "+39 0823 429111"),
        RawProvince("Salerno", "SA", "Campania", "Piazza Amendola, 1, 84121 Salerno SA", "+39 089 613111"),
        RawProvince("Ferrara", "FE", "Emilia-Romagna", "Corso Ercole I d'Este, 26, 44121 Ferrara FE", "+39 0532 294111"),
        RawProvince("Forlì", "FC", "Emilia-Romagna", "Corso Garibaldi, 173, 47121 Forlì FC", "+39 0543 719111"),
        RawProvince("Modena", "MO", "Emilia-Romagna", "Via Palmiro Togliatti, 150, 41122 Modena MO", "+39 059 410411"),
        RawProvince("Parma", "PR", "Emilia-Romagna", "Borgo della Posta, 14, 43121 Parma PR", "+39 0521 219111"),
        RawProvince("Ravenna", "RA", "Emilia-Romagna", "Viale Berlinguer, 20, 48124 Ravenna RA", "+39 0544 294111"),
        RawProvince("Reggio Emilia", "RE", "Emilia-Romagna", "Via Dante Alighieri, 10, 42121 Reggio Emilia RE", "+39 0522 457111"),
        RawProvince("Rimini", "RN", "Emilia-Romagna", "Corso d'Augusto, 154, 47921 Rimini RN", "+39 0541 437111"),
        RawProvince("Gorizia", "GO", "Friuli-Venezia Giulia", "Piazza Cavour, 1, 34170 Gorizia GO", "+39 0481 595111"),
        RawProvince("Pordenone", "PN", "Friuli-Venezia Giulia", "Piazza del Popolo, 1, 33170 Pordenone PN", "+39 0434 238111"),
        RawProvince("Trieste", "TS", "Friuli-Venezia Giulia", "Via San Sebastiano, 11, 34121 Trieste TS", "+39 040 3790111"),
        RawProvince("Udine", "UD", "Friuli-Venezia Giulia", "Viale Trieste, 46, 33100 Udine UD", "+39 0432 413111"),
        RawProvince("Frosinone", "FR", "Lazio", "Via Umberto I, 1, 03100 Frosinone FR", "+39 0775 2111"),
        RawProvince("Latina", "LT", "Lazio", "Piazza della Libertà, 1, 04100 Latina LT", "+39 0773 4031"),
        RawProvince("Rieti", "RI", "Lazio", "Largo d'Acquisto, 1, 02100 Rieti RI", "+39 0746 2991"),
        RawProvince("Viterbo", "VT", "Lazio", "Via Mariano Romiti, 1, 01100 Viterbo VT", "+39 0761 3341"),
        RawProvince("Genova", "GE", "Liguria", "Via Diaz, 2, 16121 Genova GE", "+39 010 53661"),
        RawProvince("Imperia", "IM", "Liguria", "Piazza Duomo, 14, 18100 Imperia IM", "+39 0183 7991"),
        RawProvince("La Spezia", "SP", "Liguria", "Viale Italia, 1, 19124 La Spezia SP", "+39 0187 5671"),
        RawProvince("Savona", "SV", "Liguria", "Via dei Partigiani, 3, 17100 Savona SV", "+39 019 85501"),
        RawProvince("Bergamo", "BG", "Lombardia", "Via Noli, 26, 24125 Bergamo BG", "+39 035 276111"),
        RawProvince("Brescia", "BS", "Lombardia", "Via Botticelli, 2, 25124 Brescia BS", "+39 030 37441"),
        RawProvince("Como", "CO", "Lombardia", "Viale Roosevelt, 2, 22100 Como CO", "+39 031 3171"),
        RawProvince("Cremona", "CR", "Lombardia", "Viale Trento Trieste, 6, 26100 Cremona CR", "+39 0372 4881"),
        RawProvince("Lecco", "LC", "Lombardia", "Corso Promessi Sposi, 40, 23900 Lecco LC", "+39 0341 279111"),
        RawProvince("Lodi", "LO", "Lombardia", "Piazza Castello, 1, 26900 Lodi LO", "+39 0371 4071"),
        RawProvince("Mantova", "MN", "Lombardia", "Piazza Sordello, 1, 46100 Mantova MN", "+39 0376 2051"),
        RawProvince("Monza", "MB", "Lombardia", "Via Montevecchia, 1, 20900 Monza MB", "+39 039 24101"),
        RawProvince("Pavia", "PV", "Lombardia", "Via dei Mille, 1, 27100 Pavia PV", "+39 0382 5121"),
        RawProvince("Sondrio", "SO", "Lombardia", "Via d'Adda, 1, 23100 Sondrio SO", "+39 0342 53111"),
        RawProvince("Varese", "VA", "Lombardia", "Piazza della Libertà, 1, 21100 Varese VA", "+39 0332 80111"),
        RawProvince("Ancona", "AN", "Marche", "Via Cupa, 1, 60121 Ancona AN", "+39 071 22881"),
        RawProvince("Ascoli Piceno", "AP", "Marche", "Piazza Ventidio Basso, 1, 63100 Ascoli Piceno AP", "+39 0736 3551"),
        RawProvince("Fermo", "FM", "Marche", "Corso Cavour, 1, 63900 Fermo FM", "+39 0734 2541"),
        RawProvince("Macerata", "MC", "Marche", "Via dei Velini, 1, 62100 Macerata MC", "+39 0733 2541"),
        RawProvince("Pesaro", "PU", "Marche", "Via Branca, 1, 61121 Pesaro PU", "+39 0721 3861"),
        RawProvince("Campobasso", "CB", "Molise", "Via dei Frentani, 1, 86100 Campobasso CB", "+39 0874 4021"),
        RawProvince("Isernia", "IS", "Molise", "Via Libero Testa, 1, 86170 Isernia IS", "+39 0865 4491"),
        RawProvince("Alessandria", "AL", "Piemonte", "Largo Catania, 1, 15121 Alessandria AL", "+39 0131 3101"),
        RawProvince("Asti", "AT", "Piemonte", "Via dei Mille, 1, 14100 Asti AT", "+39 0141 4181"),
        RawProvince("Biella", "BI", "Piemonte", "Via dei Mille, 1, 13900 Biella BI", "+39 015 3590111"),
        RawProvince("Cuneo", "CN", "Piemonte", "Via Malipiero, 1, 12100 Cuneo CN", "+39 0171 4431"),
        RawProvince("Novara", "NO", "Piemonte", "Via Solferino, 1, 28100 Novara NO", "+39 0321 3881"),
        RawProvince("Verbania", "VB", "Piemonte", "Via alle Fabbriche, 1, 28922 Verbania VB", "+39 0323 5111"),
        RawProvince("Vercelli", "VC", "Piemonte", "Via dei Mille, 1, 13100 Vercelli VC", "+39 0161 6021"),
        RawProvince("Bari", "BA", "Puglia", "Via Palmiro Togliatti, 1, 70124 Bari BA", "+39 080 5681111"),
        RawProvince("Barletta", "BT", "Puglia", "Via dell'Indipendenza, 1, 76121 Barletta BT", "+39 0883 578111"),
        RawProvince("Brindisi", "BR", "Puglia", "Via della Libertà, 1, 72100 Brindisi BR", "+39 0831 411111"),
        RawProvince("Foggia", "FG", "Puglia", "Via Palmiro Togliatti, 1, 71121 Foggia FG", "+39 0881 661111"),
        RawProvince("Lecce", "LE", "Puglia", "Viale Oronzo Quarta, 1, 73100 Lecce LE", "+39 0832 691111"),
        RawProvince("Taranto", "TA", "Puglia", "Via Palmiro Togliatti, 1, 74121 Taranto TA", "+39 099 4301111"),
        RawProvince("Cagliari", "CA", "Sardegna", "Via Amat, 1, 09123 Cagliari CA", "+39 070 60061"),
        RawProvince("Nuoro", "NU", "Sardegna", "Via Solferino, 1, 08100 Nuoro NU", "+39 0784 2141"),
        RawProvince("Oristano", "OR", "Sardegna", "Via Solferino, 1, 09170 Oristano OR", "+39 0783 2141"),
        RawProvince("Sassari", "SS", "Sardegna", "Via Solferino, 1, 07100 Sassari SS", "+39 079 241111"),
        RawProvince("Carbonia", "SU", "Sardegna", "Via Solferino, 1, 09013 Carbonia SU", "+39 0781 6611"),
        RawProvince("Agrigento", "AG", "Sicilia", "Via Solferino, 1, 92100 Agrigento AG", "+39 0922 401111"),
        RawProvince("Caltanissetta", "CL", "Sicilia", "Via Solferino, 1, 93100 Caltanissetta CL", "+39 0934 501111"),
        RawProvince("Catania", "CT", "Sicilia", "Via Solferino, 1, 95100 Catania CT", "+39 095 7221111"),
        RawProvince("Enna", "EN", "Sicilia", "Via Solferino, 1, 94100 Enna EN", "+39 0935 521111"),
        RawProvince("Messina", "ME", "Sicilia", "Via Solferino, 1, 98122 Messina ME", "+39 090 364111"),
        RawProvince("Palermo", "PA", "Sicilia", "Piazza della Vittoria, 1, 90134 Palermo PA", "+39 091 6551111"),
        RawProvince("Ragusa", "RG", "Sicilia", "Via Solferino, 1, 97100 Ragusa RG", "+39 0932 671111"),
        RawProvince("Siracusa", "SR", "Sicilia", "Via Solferino, 1, 96100 Siracusa SR", "+39 0931 401111"),
        RawProvince("Trapani", "TP", "Sicilia", "Via Solferino, 1, 91100 Trapani TP", "+39 0923 591111"),
        RawProvince("Arezzo", "AR", "Toscana", "Via Solferino, 1, 52100 Arezzo AR", "+39 0575 401111"),
        RawProvince("Firenze", "FI", "Toscana", "Via Zara, 2, 50129 Firenze FI", "+39 055 49771"),
        RawProvince("Grosseto", "GR", "Toscana", "Piazza della Vasca, 1, 58100 Grosseto GR", "+39 0564 391111"),
        RawProvince("Livorno", "LI", "Toscana", "Viale Boccaccio, 1, 57124 Livorno LI", "+39 0586 235111"),
        RawProvince("Lucca", "LU", "Toscana", "Viale Boccaccio, 1, 55100 Lucca LU", "+39 0583 4551"),
        RawProvince("Massa", "MS", "Toscana", "Via Boccaccio, 1, 54100 Massa MS", "+39 0585 4911"),
        RawProvince("Pisa", "PI", "Toscana", "Via Boccaccio, 1, 56125 Pisa PI", "+39 050 583111"),
        RawProvince("Pistoia", "PT", "Toscana", "Via Boccaccio, 1, 51100 Pistoia PT", "+39 0573 3711"),
        RawProvince("Prato", "PO", "Toscana", "Via Boccaccio, 1, 59100 Prato PO", "+39 0574 5551"),
        RawProvince("Siena", "SI", "Toscana", "Via Boccaccio, 1, 53100 Siena SI", "+39 0577 294111"),
        RawProvince("Bolzano", "BZ", "Trentino-Alto Adige", "Largo d'Acquisto, 1, 39100 Bolzano BZ", "+39 0471 947111"),
        RawProvince("Trento", "TN", "Trentino-Alto Adige", "Viale Boccaccio, 1, 38122 Trento TN", "+39 0461 894111"),
        RawProvince("Perugia", "PG", "Umbria", "Via Boccaccio, 1, 06121 Perugia PG", "+39 075 506111"),
        RawProvince("Terni", "TR", "Umbria", "Via Boccaccio, 1, 05100 Terni TR", "+39 0744 4801"),
        RawProvince("Aosta", "AO", "Valle d'Aosta", "Corso Battaglione, 1, 11100 Aosta AO", "+39 0165 271111"),
        RawProvince("Belluno", "BL", "Veneto", "Viale Boccaccio, 1, 32100 Belluno BL", "+39 0437 941111"),
        RawProvince("Padova", "PD", "Veneto", "Via Palmiro Togliatti, 1, 35121 Padova PD", "+39 049 833111"),
        RawProvince("Rovigo", "RO", "Veneto", "Viale Boccaccio, 1, 45100 Rovigo RO", "+39 0425 201111"),
        RawProvince("Treviso", "TV", "Veneto", "Viale Boccaccio, 1, 31100 Treviso TV", "+39 0422 248111"),
        RawProvince("Venezia", "VE", "Veneto", "Via Palmiro Togliatti, 1, 30175 Venezia VE", "+39 041 2715511"),
        RawProvince("Verona", "VR", "Veneto", "Lungadige Galtarossa, 11, 37133 Verona VR", "+39 045 8090411"),
        RawProvince("Vicenza", "VI", "Veneto", "Viale Boccaccio, 1, 36100 Vicenza VI", "+39 0444 337111")
    )

    private val handcraftedQuesture: List<Questura> = listOf(
        Questura(
            id = "milano",
            name = "Questura di Milano - Ufficio Immigrazione",
            province = "Milano",
            provinceCode = "MI",
            region = "Lombardia",
            address = "Via Fatebenefratelli, 11, 20121 Milano MI",
            phone = "+39 02 62261",
            email = "immigrazione.milano@poliziadistato.it",
            pec = "immig.quest.mi@pecps.poliziadistato.it",
            website = "https://questure.poliziadistato.it/Milano",
            googleMapsUrl = "https://maps.google.com/?q=Questura+di+Milano",
            portalAvailability = "Both",
            officeNotes = LocalizedText(
                it = "L'Ufficio Immigrazione centrale gestisce i permessi ordinari. I tempi di attesa sono significativi.",
                en = "The central Immigration Office handles standard permits. Expect significant waiting times.",
                ar = "يتعامل مكتب الهجرة المركزي مع التصاريح العادية. توقع أوقات انتظار طويلة.",
                es = "La Oficina de Extranjería central gestiona los permisos ordinarios. Tiempos de espera significativos.",
                fr = "Le bureau central de l'immigration gère i titres ordinari. Les temps d'attente sont importants."
            ),
            openingHours = LocalizedText(
                it = "Lunedì - Venerdì: 08:00 - 13:00 (Solo su appuntamento)",
                en = "Monday - Friday: 08:00 - 13:00 (Appointment only)",
                ar = "الإثنين - الجمعة: 08:00 - 13:00 (بموعد مسبق فقط)",
                es = "Lunes - Viernes: 08:00 - 13:00 (Solo con cita)",
                fr = "Lundi - Vendredi: 08:00 - 13:00 (Sur rendez-vous uniquement)"
            ),
            appointmentMethod = LocalizedText(
                it = "Convocazione tramite SMS o portale PrenotaFacile per categorie specifiche.",
                en = "Summons via SMS or PrenotaFacile portal for specific categories.",
                ar = "الاستدعاء عبر الرسائل القصيرة SMS أو بوابة PrenotaFacile لفئات محددة.",
                es = "Convocatoria por SMS o portal PrenotaFacile para categorías específicas.",
                fr = "Convocation par SMS ou portail PrenotaFacile pour certaines catégories."
            ),
            averageWaitingTime = LocalizedText(
                it = "5 - 7 Mesi",
                en = "5 - 7 Months",
                ar = "٥ - ٧ أشهر",
                es = "5 - 7 Meses",
                fr = "5 - 7 Mois"
            ),
            localWarnings = LocalizedText(
                it = "Lungo tempo di attesa la mattina. Si consiglia di arrivare con 15 minuti di anticipo rispetto all'orario di convocazione.",
                en = "Long queues in the morning. It is recommended to arrive 15 minutes before your scheduled appointment.",
                ar = "طوابير طويلة في الصباح. يوصى بالوصول قبل الموعد بـ ١٥ دقيقة.",
                es = "Largas colas por la mañana. Se recomienda llegar 15 minutos antes de la hora citada.",
                fr = "Longues files d'attente le matin. Il est recommandé d'arriver 15 minutes avant l'heure de votre convocation."
            ),
            communityNotes = LocalizedText(
                it = "Molti utenti segnalano che la consegna dei permessi pronti avviene principalmente il mercoledì pomeriggio.",
                en = "Many users report that ready permits are delivered mainly on Wednesday afternoons.",
                ar = "يفيد العديد من المستخدمين أن تسليم التصاريح الجاهزة يتم بشكل أساسي بعد ظهر يوم الأربعاء.",
                es = "Muchos usuarios informan que la entrega de permisos listos se realiza principalmente los miércoles por la tarde.",
                fr = "De nombreux utilisateurs signalent que la remise des titres prêts a lieu principalement le mercredi après-midi."
            )
        ),
        Questura(
            id = "roma",
            name = "Questura di Roma - Ufficio Immigrazione",
            province = "Roma",
            provinceCode = "RM",
            region = "Lazio",
            address = "Via Teofilo Patini, 20, 00155 Roma RM",
            phone = "+39 06 46861",
            email = "immigrazione.roma@poliziadistato.it",
            pec = "immig.quest.rm@pecps.poliziadistato.it",
            website = "https://questure.poliziadistato.it/Roma",
            googleMapsUrl = "https://maps.google.com/?q=Questura+di+Roma",
            portalAvailability = "Both",
            officeNotes = LocalizedText(
                it = "Sede principale di Via Patini. Richiede lo spostamento in periferia est.",
                en = "Main office located in Via Patini. Located in the eastern suburbs of Rome.",
                ar = "المكتب الرئيسي يقع في شارع باتيني. يقع في الضواحي الشرقية لروما.",
                es = "Oficina principal ubicada en Via Patini. Situada en la periferia este de Roma.",
                fr = "Bureau principal situé Via Patini. Situé dans la banlieue est de Rome."
            ),
            openingHours = LocalizedText(
                it = "Lunedì - Venerdì: 08:00 - 14:00 (Solo su appuntamento)",
                en = "Monday - Friday: 08:00 - 14:00 (Appointment only)",
                ar = "الإثنين - الجمعة: 08:00 - 14:00 (بموعد مسبق فقط)",
                es = "Lunes - Viernes: 08:00 - 14:00 (Solo con cita)",
                fr = "Lundi - Vendredi: 08:00 - 14:00 (Sur rendez-vous uniquement)"
            ),
            appointmentMethod = LocalizedText(
                it = "Invio raccomandata postale o portale Cupa-Project.",
                en = "Post office registered mail or Cupa-Project portal booking.",
                ar = "إرسال بريد مسجل أو الحجز عبر بوابة Cupa-Project.",
                es = "Envío de carta certificada o reserva en el portal Cupa-Project.",
                fr = "Envoi par courrier recommandé ou réservation sur le portail Cupa-Project."
            ),
            averageWaitingTime = LocalizedText(
                it = "6 - 8 Mesi",
                en = "6 - 8 Months",
                ar = "٦ - ٨ أشهر",
                es = "6 - 8 Meses",
                fr = "6 - 8 Mois"
            ),
            localWarnings = LocalizedText(
                it = "Ufficio molto affollato. Controllare bene i documenti prima di presentarsi allo sportello.",
                en = "Extremely busy office. Ensure all documents are complete before approaching the counter.",
                ar = "المكتب مزدحم للغاية. يرجى التأكد من اكتمال المستندات قبل التوجه إلى الشباك.",
                es = "Oficina muy concurrida. Verifique bien los documentos antes de acudir a la ventanilla.",
                fr = "Bureau très fréquenté. Vérifiez attentivement vos documents avant de vous présenter au guichet."
            ),
            communityNotes = LocalizedText(
                it = "I controlli di sicurezza all'ingresso sono rigorosi. Non è permesso introdurre borse ingombranti.",
                en = "Security checks at the entrance are rigorous. Bulky bags are not allowed inside.",
                ar = "الفحوصات الأمنية عند المدخل صارمة. لا يُسمح بإدخال الحقائب الضخمة.",
                es = "Los controles de seguridad en la entrada son rigurosos. No se permite ingresar bolsos voluminosos.",
                fr = "Les contrôles de sécurité à l'entrée sont rigoureux. Les sacs encombrants ne sont pas autorisés."
            )
        ),
        Questura(
            id = "torino",
            name = "Questura di Torino - Ufficio Immigrazione",
            province = "Torino",
            provinceCode = "TO",
            region = "Piemonte",
            address = "Corso Verona, 4, 10152 Torino TO",
            phone = "+39 011 55881",
            email = "immigrazione.torino@poliziadistato.it",
            pec = "immig.quest.to@pecps.poliziadistato.it",
            website = "https://questure.poliziadistato.it/Torino",
            googleMapsUrl = "https://maps.google.com/?q=Questura+di+Torino",
            portalAvailability = "Both",
            officeNotes = LocalizedText(
                it = "Ufficio Immigrazione situato nei pressi del centro, facilmente raggiungibile con mezzi pubblici.",
                en = "Immigration Office located near the center, easily reachable by public transport.",
                ar = "مكتب الهجرة يقع بالقرب من وسط المدينة، يسهل الوصول إليه بوسائل النقل العام.",
                es = "Oficina de extranjería situada cerca del centro, de fácil acceso en transporte público.",
                fr = "Bureau de l'immigration situé près du centre, facilement accessible en transports en commun."
            ),
            openingHours = LocalizedText(
                it = "Lunedì - Venerdì: 08:30 - 13:30 (Solo convocati)",
                en = "Monday - Friday: 08:30 - 13:30 (Appointment only)",
                ar = "الإثنين - الجمعة: 08:30 - 13:30 (للمستدعين فقط)",
                es = "Lunes - Viernes: 08:30 - 13:30 (Solo convocados)",
                fr = "Lundi - Vendredi: 08:30 - 13:30 (Convoqués uniquement)"
            ),
            appointmentMethod = LocalizedText(
                it = "Sito Poste Italiane o sistema interno di convocazione della Questura.",
                en = "Poste Italiane kit or internal Questura summons system.",
                ar = "مغلف البريد الإيطالي أو نظام الاستدعاء الداخلي للكويستورا.",
                es = "Kit de Poste Italiane o sistema de convocatoria interna de la Questura.",
                fr = "Kit Poste Italiane ou système de convocation interne de la Questura."
            ),
            averageWaitingTime = LocalizedText(
                it = "4 - 6 Mesi",
                en = "4 - 6 Months",
                ar = "٤ - ٦ أشهر",
                es = "4 - 6 Meses",
                fr = "4 - 6 Mois"
            ),
            localWarnings = LocalizedText(
                it = "Le impronte digitali vengono prese di solito al mattino presto, portarsi acqua e snack.",
                en = "Fingerprinting is usually conducted early in the morning, bring water and snacks.",
                ar = "عادة ما يتم أخذ البصمات في الصباح الباكر، يرجى إحضار الماء وبعض الوجبات الخفيفة.",
                es = "Las huellas dactilares se suelen tomar temprano por la mañana, traiga agua y bocadillos.",
                fr = "Les empreintes digitales sont généralement prises tôt le matin, apportez de l'eau et des collations."
            ),
            communityNotes = LocalizedText(
                it = "Ottima organizzazione delle code esterne grazie al personale di vigilanza.",
                en = "Excellent organization of outdoor queues thanks to security personnel.",
                ar = "تنظيم ممتاز للطوابير الخارجية بفضل موظفي الأمن.",
                es = "Excelente organización de las colas exteriores gracias al personal de seguridad.",
                fr = "Excellente organisation des files d'attente extérieures grâce au personnel de sécurité."
            )
        ),
        Questura(
            id = "piacenza",
            name = "Questura di Piacenza - Ufficio Immigrazione",
            province = "Piacenza",
            provinceCode = "PC",
            region = "Emilia-Romagna",
            address = "Viale Malta, 10c, 29121 Piacenza PC",
            phone = "+39 0523 397111",
            email = "immigrazione.piacenza@poliziadistato.it",
            pec = "immig.quest.pc@pecps.poliziadistato.it",
            website = "https://questure.poliziadistato.it/Piacenza",
            googleMapsUrl = "https://maps.google.com/?q=Questura+di+Piacenza",
            portalAvailability = "Both",
            officeNotes = LocalizedText(
                it = "L'ufficio immigrazione di Piacenza è noto per tempi relativamente più veloci rispetto alle grandi metropoli.",
                en = "The immigration office of Piacenza is known for relatively faster processing times compared to major metropolises.",
                ar = "يشتهر مكتب الهجرة في بياشينزا بأوقات معالجة أسرع نسبياً مقارنة بالمدن الكبرى.",
                es = "La oficina de extranjería de Piacenza es conocida por tiempos relativamente más rápidos en comparación con las grandes metrópolis.",
                fr = "Le bureau de l'immigration de Piacenza est connu pour ses délais de traitement relativement plus rapides que ceux des grandes métropoles."
            ),
            openingHours = LocalizedText(
                it = "Lunedì - Venerdì: 08:30 - 12:30, Giovedì pomeriggio: 15:00 - 17:00 (Ritiro permessi)",
                en = "Monday - Friday: 08:30 - 12:30, Thursday afternoon: 15:00 - 17:00 (Permit pickup)",
                ar = "الإثنين - الجمعة: 08:30 - 12:30، بعد ظهر الخميس: 15:00 - 17:00 (استلام التصاريح)",
                es = "Lunes - Viernes: 08:30 - 12:30, Jueves por la tarde: 15:00 - 17:00 (Entrega de permisos)",
                fr = "Lundi - Vendredi: 08:30 - 12:30, Jeudi après-midi: 15:00 - 17:00 (Retrait des titres)"
            ),
            appointmentMethod = LocalizedText(
                it = "Convocazione postale o appuntamenti diretti registrati tramite portale immigrazione.",
                en = "Postal summons or direct appointments booked via the immigration portal.",
                ar = "الاستدعاء البريدي أو المواعيد المباشرة المحجوزة عبر بوابة الهجرة.",
                es = "Convocatoria postal o citas directas reservadas a través del portal de extranjería.",
                fr = "Convocation postale ou rendez-vous directs pris via le portail de l'immigration."
            ),
            averageWaitingTime = LocalizedText(
                it = "2 - 4 Mesi",
                en = "2 - 4 Months",
                ar = "٢ - ٤ أشهر",
                es = "2 - 4 Meses",
                fr = "2 - 4 Mois"
            ),
            localWarnings = LocalizedText(
                it = "Ufficio molto ordinato. Si consiglia comunque di portare copie extra di tutta la documentazione per evitare rinvii.",
                en = "Highly organized office. It is still recommended to bring extra copies of all documentation to avoid delays.",
                ar = "مكتب منظم للغاية. ومع ذلك، يوصى بإحضار نسخ إضافية من جميع المستندات لتجنب التأخير.",
                es = "Oficina muy ordenada. No obstante, se recomienda llevar copias adicionales de toda la documentación para evitar retrasos.",
                fr = "Bureau très ordonné. Il est tout de même recommandé d'apporter des copies supplémentaires de tous les documents pour éviter des reports."
            ),
            communityNotes = LocalizedText(
                it = "I parcheggi gratuiti in Viale Malta sono limitati. Arrivate in anticipo se venite in auto.",
                en = "Free parking spaces on Viale Malta are limited. Arrive early if driving.",
                ar = "مواقف السيارات المجانية في Viale Malta محدودة. يرجى الوصول مبكراً إذا كنت تقود السيارة.",
                es = "Las plazas de aparcamiento gratuitas en Viale Malta son limitadas. Llegue temprano si viene en coche.",
                fr = "Les places de stationnement gratuites sur Viale Malta sont limitées. Arrivez tôt si vous venez en voiture."
            )
        ),
        Questura(
            id = "bologna",
            name = "Questura di Bologna - Ufficio Immigrazione",
            province = "Bologna",
            provinceCode = "BO",
            region = "Emilia-Romagna",
            address = "Via Nicolò dall'Arca, 20, 40129 Bologna BO",
            phone = "+39 051 6401111",
            email = "immigrazione.bologna@poliziadistato.it",
            pec = "immig.quest.bo@pecps.poliziadistato.it",
            website = "https://questure.poliziadistato.it/Bologna",
            googleMapsUrl = "https://maps.google.com/?q=Questura+di+Bologna",
            portalAvailability = "Both",
            officeNotes = LocalizedText(
                it = "Ufficio situato in Via Nicolò dall'Arca. Gestione efficiente ma volumi elevati.",
                en = "Office located in Via Nicolò dall'Arca. Efficient management but very high caseload volumes.",
                ar = "يقع المكتب في شارع نيكولو دال أركا. إدارة فعالة ولكن مع حجم معاملات كبير.",
                es = "Oficina situada en Via Nicolò dall'Arca. Gestión eficiente pero volumen de casos muy alto.",
                fr = "Bureau situé Via Nicolò dall'Arca. Gestion efficace mais volumes de dossiers très élevés."
            ),
            openingHours = LocalizedText(
                it = "Lunedì - Venerdì: 08:00 - 13:00 (Solo convocati)",
                en = "Monday - Friday: 08:00 - 13:00 (Appointment only)",
                ar = "الإثنين - الجمعة: 08:00 - 13:00 (للمستدعين فقط)",
                es = "Lunes - Viernes: 08:00 - 13:00 (Solo convocados)",
                fr = "Lundi - Vendredi: 08:00 - 13:00 (Convoqués uniquement)"
            ),
            appointmentMethod = LocalizedText(
                it = "Portale cupa-project.it e convocazione postale.",
                en = "Cupa-project.it portal and postal convocations.",
                ar = "بوابة cupa-project.it والاستدعاء البريدي.",
                es = "Portal cupa-project.it y convocatorias postales.",
                fr = "Portail cupa-project.it et convocations postales."
            ),
            averageWaitingTime = LocalizedText(
                it = "4 - 5 Mesi",
                en = "4 - 5 Months",
                ar = "٤ - ٥ أشهر",
                es = "4 - 5 Meses",
                fr = "4 - 5 Mois"
            ),
            localWarnings = LocalizedText(
                it = "Verificare regolarmente la PEC se fornita durante l'istruttoria. Spesso inviano richieste di integrazione digitale.",
                en = "Check your PEC certified email regularly if provided during review. They frequently request digital integrations.",
                ar = "تحقق بانتظام من البريد الإلكتروني المعتمد PEC إذا تم تقديمه أثناء المراجعة. غالباً ما يرسلون طلبات استكمال رقمية.",
                es = "Verifique regularmente el PEC si lo proporcionó durante el trámite. Suelen enviar solicitudes de integración digital.",
                fr = "Vérifiez régulièrement votre courrier certifié PEC s'il a été fourni lors de l'instruction. Ils envoient fréquemment des demandes de pièces complémentaires."
            ),
            communityNotes = LocalizedText(
                it = "All'ingresso è presente una bacheca con i numeri dei permessi pronti suddivisi per data d'invio.",
                en = "At the entrance, there is a notice board listing ready permit numbers categorized by submission date.",
                ar = "عند المدخل توجد لوحة إعلانات تسرد أرقام التصاريح الجاهزة مصنفة حسب تاريخ الإرسال.",
                es = "En la entrada hay un tablón de anuncios con los números de permisos listos clasificados por fecha de envío.",
                fr = "À l'entrée, un panneau d'affichage indique les numéros des titres prêts classés par date d'envoi."
            )
        ),
        Questura(
            id = "napoli",
            name = "Questura di Napoli - Ufficio Immigrazione",
            province = "Napoli",
            provinceCode = "NA",
            region = "Campania",
            address = "Via Marchese Campodisola, 21, 80133 Napoli NA",
            phone = "+39 081 7941111",
            email = "immigrazione.napoli@poliziadistato.it",
            pec = "immig.quest.na@pecps.poliziadistato.it",
            website = "https://questure.poliziadistato.it/Napoli",
            googleMapsUrl = "https://maps.google.com/?q=Questura+di+Napoli",
            portalAvailability = "Both",
            officeNotes = LocalizedText(
                it = "Ufficio centrale molto affollato. Gestisce tutta la provincia partenopea.",
                en = "Very crowded central office. Handles the entire Neapolitan province.",
                ar = "مكتب مركزي مزدحم للغاية. يتعامل مع مقاطعة نابولي بأكملها.",
                es = "Oficina central muy concurrida. Gestiona toda la provincia napolitana.",
                fr = "Bureau central très encombré. Gère toute la province de Naples."
            ),
            openingHours = LocalizedText(
                it = "Lunedì - Venerdì: 08:30 - 12:30 (Solo convocati)",
                en = "Monday - Friday: 08:30 - 12:30 (Appointment only)",
                ar = "الإثنين - الجمعة: 08:30 - 12:30 (للمستدعين فقط)",
                es = "Lunes - Viernes: 08:30 - 12:30 (Solo convocados)",
                fr = "Lundi - Vendredi: 08:30 - 12:30 (Convoqués uniquement)"
            ),
            appointmentMethod = LocalizedText(
                it = "Convocazioni tramite raccomandata e SMS Poste.",
                en = "Convocations via registered mail and Post Office SMS.",
                ar = "الاستدعاءات عبر البريد المسجل ورسائل البريد القصيرة SMS.",
                es = "Convocatorias por correo certificado y SMS de Correos.",
                fr = "Convocations par courrier recommandé et SMS de la Poste."
            ),
            averageWaitingTime = LocalizedText(
                it = "5 - 8 Mesi",
                en = "5 - 8 Months",
                ar = "٥ - ٨ أشهر",
                es = "5 - 8 Meses",
                fr = "5 - 8 Mois"
            ),
            localWarnings = LocalizedText(
                it = "Si consiglia la massima puntualità. Le code esterne iniziano molto presto.",
                en = "Punctuality is highly recommended. Outdoor queues start forming very early.",
                ar = "يوصى بشدة بالالتزام بالمواعيد بدقة. تبدأ الطوابير الخارجية في وقت مبكر جداً.",
                es = "Se recomienda máxima puntualidad. Las colas exteriores empiezan muy temprano.",
                fr = "La ponctualité est vivement recommandée. Les files d'attente à l'extérieur se forment très tôt."
            ),
            communityNotes = LocalizedText(
                it = "Gli uffici distaccati dei Commissariati di zona sono a volte abilitati alla consegna dei permessi pronti.",
                en = "Detached offices of local police stations are sometimes authorized for ready permit deliveries.",
                ar = "يُصرح أحياناً للمكاتب الفرعية لمراكز الشرطة المحلية بتسليم التصاريح الجاهزة.",
                es = "Las oficinas delegadas de las comisarías locales a veces están autorizadas para entregar los permisos listos.",
                fr = "Les bureaux détachés des commissariats locaux sont parfois habilités à remettre les titres prêts."
            )
        )
    )

    val questure: List<Questura> = buildList {
        // First add the handcrafted ones
        addAll(handcraftedQuesture)
        
        // Find existing handcrafted codes to avoid duplicating
        val handcraftedCodes = handcraftedQuesture.map { it.provinceCode.uppercase() }.toSet()
        
        for (raw in rawProvinces) {
            if (raw.code.uppercase() in handcraftedCodes) continue
            
            val cleanId = raw.capital.lowercase().replace(" ", "_").replace("'", "")
            val name = "Questura di ${raw.capital} - Ufficio Immigrazione"
            val pec = "immig.quest.${raw.code.lowercase()}@pecps.poliziadistato.it"
            val websiteCapital = raw.capital.replace(" ", "").replace("'", "")
            val website = "https://questure.poliziadistato.it/$websiteCapital"
            val googleMapsUrl = "https://maps.google.com/?q=Questura+di+${raw.capital.replace(" ", "+").replace("'", "+")}"
            
            add(
                Questura(
                    id = cleanId,
                    name = name,
                    province = raw.capital,
                    provinceCode = raw.code,
                    region = raw.region,
                    address = raw.address,
                    phone = raw.phone,
                    email = "immigrazione.${raw.capital.lowercase().replace(" ", "").replace("'", "")}@poliziadistato.it",
                    pec = pec,
                    website = website,
                    googleMapsUrl = googleMapsUrl,
                    portalAvailability = "Both",
                    officeNotes = LocalizedText(
                        it = "Ufficio immigrazione della provincia di ${raw.capital}.",
                        en = "Immigration office for the province of ${raw.capital}.",
                        ar = "مكتب الهجرة لمقاطعة ${raw.capital}.",
                        es = "Oficina de extranjería para la provincia de ${raw.capital}.",
                        fr = "Bureau de l'immigration pour la province de ${raw.capital}."
                    ),
                    openingHours = LocalizedText(
                        it = "Lunedì - Venerdì: 08:30 - 12:30 (Solo su appuntamento)",
                        en = "Monday - Friday: 08:30 - 12:30 (Appointment only)",
                        ar = "الإثنين - الجمعة: 08:30 - 12:30 (بموعد مسبق فقط)",
                        es = "Lunes - Viernes: 08:30 - 12:30 (Solo con cita)",
                        fr = "Lundi - Vendredi: 08:30 - 12:30 (Sur rendez-vous uniquement)"
                    ),
                    appointmentMethod = LocalizedText(
                        it = "Convocazione tramite SMS o portale dedicato.",
                        en = "Summons via SMS or dedicated portal.",
                        ar = "الاستدعاء عبر الرسائل القصيرة SMS أو البوابة المخصصة.",
                        es = "Convocatoria por SMS o portal dedicado.",
                        fr = "Convocation par SMS ou portail dédié."
                    ),
                    averageWaitingTime = LocalizedText(
                        it = "3 - 5 Mesi",
                        en = "3 - 5 Months",
                        ar = "٣ - ٥ أشهر",
                        es = "3 - 5 Meses",
                        fr = "3 - 5 Mois"
                    ),
                    localWarnings = LocalizedText(
                        it = "Si consiglia di arrivare con 15 minuti di anticipo rispetto all'orario convocato.",
                        en = "It is recommended to arrive 15 minutes before your scheduled appointment.",
                        ar = "يوصى بالوصول قبل الموعد المحدد بـ ١٥ دقيقة.",
                        es = "Se recomienda llegar 15 minutos antes de la hora citada.",
                        fr = "Il est recommandé d'arriver 15 minutes avant l'heure de votre convocation."
                    ),
                    communityNotes = LocalizedText(
                        it = "Presentarsi con tutta la documentazione in fotocopia e originale.",
                        en = "Bring all required documents in both original and photocopy format.",
                        ar = "يرجى إحضار جميع المستندات المطلوبة بنسخها الأصلية والمصورة.",
                        es = "Presentarse con todos los documentos originales y fotocopias.",
                        fr = "Veuillez apporter tous les documents requis en original et en photocopie."
                    )
                )
            )
        }
    }

    fun getById(id: String): Questura? {
        return questure.find { it.id.lowercase() == id.lowercase() }
    }

    fun search(query: String): List<Questura> {
        if (query.isBlank()) return questure
        val q = query.lowercase().trim()
        return questure.filter {
            it.name.lowercase().contains(q) ||
            it.province.lowercase().contains(q) ||
            it.provinceCode.lowercase().contains(q) ||
            it.region.lowercase().contains(q) ||
            it.address.lowercase().contains(q)
        }
    }
}
