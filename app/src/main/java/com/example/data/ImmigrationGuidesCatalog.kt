package com.example.data

object ImmigrationGuidesCatalog {
    val guides: List<ImmigrationGuide> = listOf(
        // 1. WORK PERMIT
        ImmigrationGuide(
            id = "work_permit",
            category = "work",
            iconName = "work",
            title = LocalizedText(
                it = "Permesso di Soggiorno per Lavoro Subordinato",
                en = "Work Permit (Subordinate Employment)",
                ar = "تصريح العمل (العمل التابع)",
                es = "Permiso de Trabajo Subordinado",
                fr = "Titre de Séjour pour Travail Salarié"
            ),
            description = LocalizedText(
                it = "Guida completa per richiedere o aggiornare il permesso per lavoro subordinato.",
                en = "Complete guide to apply for or update a work permit for subordinate employment.",
                ar = "دليل كامل للتقدم بطلب أو تحديث تصريح العمل للعمل التابع.",
                es = "Guía completa para solicitar o actualizar el permiso de trabajo subordinado.",
                fr = "Guide complet pour demander ou mettre à jour un titre de séjour salarié."
            ),
            difficulty = LocalizedText(
                it = "Medio", en = "Medium", ar = "متوسط", es = "Medio", fr = "Moyen"
            ),
            estimatedDuration = LocalizedText(
                it = "3 - 6 Mesi", en = "3 - 6 Months", ar = "٣ - ٦ أشهر", es = "3 - 6 Meses", fr = "3 - 6 Mois"
            ),
            estimatedCosts = LocalizedText(
                it = "ca. €150", en = "approx. €150", ar = "حوالي ١٥٠ يورو", es = "aprox. €150", fr = "env. €150"
            ),
            validityPeriod = LocalizedText(
                it = "1 - 2 Anni", en = "1 - 2 Years", ar = "سنة - سنتين", es = "1 - 2 Años", fr = "1 - 2 Ans"
            ),
            requiredOffices = listOf("Poste Italiane", "Questura", "Sportello Unico Immigrazione"),
            overview = GuideOverview(
                whoIsItFor = LocalizedText(
                    it = "Cittadini non-UE assunti da un datore di lavoro in Italia.",
                    en = "Non-EU citizens employed by an employer in Italy.",
                    ar = "مواطنو الدول غير الأعضاء في الاتحاد الأوروبي الذين يوظفهم صاحب عمل في إيطاليا.",
                    es = "Ciudadanos no comunitarios contratados por un empleador en Italia.",
                    fr = "Citoyens non-UE embauchés par un employeur en Italie."
                ),
                whoCanApply = LocalizedText(
                    it = "Lavoratori con contratto valido ed entrate regolari tramite quote d'ingresso (Decreto Flussi) o conversioni.",
                    en = "Workers with a valid contract and regular income via entrance quotas (Decreto Flussi) or conversion.",
                    ar = "العمال الذين لديهم عقد ساري ومستوى دخل منتظم عبر حصص الدخول (Decreto Flussi) أو تحويل التأشيرة.",
                    es = "Trabajadores con contrato válido e ingresos regulares a través de cuotas de entrada (Decreto Flussi) o conversiones.",
                    fr = "Travailleurs disposant d'un contrat valide et de revenus réguliers via les quotas d'entrée (Decreto Flussi) ou une conversion."
                ),
                mainRequirements = listOf(
                    LocalizedText(
                        it = "Contratto di soggiorno o proposta di assunzione valida.",
                        en = "Valid residence contract or job offer.",
                        ar = "عقد إقامة أو عرض عمل ساري المفعول.",
                        es = "Contrato de residencia o propuesta de trabajo válida.",
                        fr = "Contrat de séjour ou offre d'emploi valide."
                    ),
                    LocalizedText(
                        it = "Idoneità alloggiativa (certificazione di conformità dell'alloggio).",
                        en = "Housing suitability certificate (Idoneità Alloggiativa).",
                        ar = "شهادة ملاءمة السكن (Idoneità Alloggiativa).",
                        es = "Certificado de habitabilidad de la vivienda (Idoneità Alloggiativa).",
                        fr = "Certificat de conformité du logement (Idoneità Alloggiativa)."
                    ),
                    LocalizedText(
                        it = "Reddito minimo annuo non inferiore all'assegno sociale.",
                        en = "Minimum annual income not lower than the social allowance (Assegno Sociale).",
                        ar = "الحد الأدنى للدخل السنوي لا يقل عن مبلغ الضمان الاجتماعي (Assegno Sociale).",
                        es = "Ingresos anuales mínimos no inferiores al subsidio social (Assegno Sociale).",
                        fr = "Revenu annuel minimum non inférieur à l'allocation sociale (Assegno Sociale)."
                    )
                )
            ),
            timeline = listOf(
                GuideTimelineStep(
                    1,
                    LocalizedText(it = "Firma del Contratto", en = "Contract Signing", ar = "توقيع العقد", es = "Firma de Contrato", fr = "Signature du Contrat"),
                    LocalizedText(
                        it = "Firma il contratto di lavoro subordinato con il datore di lavoro presso lo Sportello Unico.",
                        en = "Sign the employment contract with your employer at the Sportello Unico Immigrazione.",
                        ar = "وقع عقد العمل مع صاحب العمل في مكتب الهجرة الموحد.",
                        es = "Firma el contrato con tu empleador en el Sportello Unico Immigrazione.",
                        fr = "Signez le contrat de travail avec votre employeur au Sportello Unico."
                    )
                ),
                GuideTimelineStep(
                    2,
                    LocalizedText(it = "Invio del Kit Giallo", en = "Postal Kit Submission", ar = "إرسال المغلف البريدي الأصفر", es = "Envío del Kit Postal", fr = "Envoi du Kit Postal"),
                    LocalizedText(
                        it = "Ritira il 'Kit Giallo' alle Poste, compilalo con i documenti e spediscilo dallo sportello 'Amico'.",
                        en = "Collect the 'Yellow Kit' from the Post Office, fill it out with requested documents and send it.",
                        ar = "خذ المغلف الأصفر من مكتب البريد، واملأه بالمستندات المطلوبة ثم أرسله عبر خدمة Sportello Amico.",
                        es = "Recoge el 'Kit Giallo' en la Oficina de Correos, complétalo con tus documentos y envíalo.",
                        fr = "Récupérez le 'Kit Giallo' à la Poste, remplissez-le et envoyez-le via le guichet 'Amico'."
                    )
                ),
                GuideTimelineStep(
                    3,
                    LocalizedText(it = "Appuntamento in Questura", en = "Questura Fingerprinting", ar = "موعد البصمات في الكويستورا", es = "Cita de Huellas", fr = "Rendez-vous à la Préfecture"),
                    LocalizedText(
                        it = "Presentati alla Questura per il rilievo delle impronte digitali portando gli originali.",
                        en = "Show up at the Questura for fingerprinting. Make sure to bring all original documents.",
                        ar = "اذهب إلى الكويستورا لأخذ البصمات الرقمية مع إحضار أصول المستندات.",
                        es = "Preséntate en la Questura para la toma di huellas dactilares llevando los originales.",
                        fr = "Présentez-vous à la Questura pour la prise d'empreintes digitales en apportant les originaux."
                    )
                ),
                GuideTimelineStep(
                    4,
                    LocalizedText(it = "Attesa e Monitoraggio", en = "Processing Status", ar = "الانتظار والمتابعة", es = "Procesamiento y Espera", fr = "Traitement et Suivi"),
                    LocalizedText(
                        it = "Monitora l'avanzamento online su Soggiorno Track usando il codice assicurata.",
                        en = "Monitor the application's progress on Soggiorno Track using your post receipt number.",
                        ar = "تابع حالة المعاملة على تطبيق Soggiorno Track باستخدام رقم الإيصال.",
                        es = "Sigue el estado del trámite en Soggiorno Track usando el número de recibo de correos.",
                        fr = "Suivez l'avancement du dossier sur Soggiorno Track en utilisant le numéro de reçu postal."
                    )
                ),
                GuideTimelineStep(
                    5,
                    LocalizedText(it = "Ritiro della Carta", en = "Card Collection", ar = "استلام البطاقة الإلكترونية", es = "Recogida de la Tarjeta", fr = "Retrait de la Carte"),
                    LocalizedText(
                        it = "Ritira il permesso plastificato non appena lo stato diventa 'Pronto per la consegna'.",
                        en = "Collect your plastic residency permit once the status turns to 'Pronto per la consegna'.",
                        ar = "استلم بطاقة الإقامة الإلكترونية فور تغير الحالة إلى 'جاهز للتسليم'.",
                        es = "Recoge el permesso plastificado cuando el estado cambie a 'Listo para la entrega'.",
                        fr = "Retirez votre carte de séjour dès que le statut devient 'Prêt pour la remise'."
                    )
                )
            ),
            documents = listOf(
                GuideDocument("doc_passport", LocalizedText(it = "Passaporto", en = "Passport", ar = "جواز السفر", es = "Pasaporte", fr = "Passeport"), LocalizedText(it = "Copia di tutte le pagine scritte e timbrate.", en = "Copy of all pages containing stamps/visas.", ar = "نسخة من جميع صفحات جواز السفر المكتوب عليها والمختومة.", es = "Copia de todas las páginas escritas y selladas.", fr = "Copie de toutes les pages écrites et tamponnées.")),
                GuideDocument("doc_contract", LocalizedText(it = "Contratto di Lavoro", en = "Employment Contract", ar = "عقد العمل", es = "Contrato de Trabajo", fr = "Contrat de Travail"), LocalizedText(it = "Unilav o contratto firmato e ultima busta paga.", en = "Unilav form or signed contract and recent payslip.", ar = "نموذج Unilav أو عقد موقع وآخر قسيمة راتب.", es = "Formulario Unilav o contrato firmado y última nómina.", fr = "Formulaire Unilav ou contrat signé et fiche de paie récente.")),
                GuideDocument("doc_cud", LocalizedText(it = "Certificazione Unica / CUD", en = "Tax Statement (CUD)", ar = "شهادة الدخل الضريبي CUD", es = "Certificación de Impuestos (CUD)", fr = "Justificatif de Revenus (CUD)"), LocalizedText(it = "Dichiarazione dei redditi dell'anno precedente.", en = "Tax returns or income statement for the previous year.", ar = "الإقرار الضريبي السنوي للعام المنصرم.", es = "Declaración de ingresos del año anterior.", fr = "Déclaration de revenus de l'année précédente.")),
                GuideDocument("doc_housing", LocalizedText(it = "Cessione di Fabbricato", en = "Housing Declaration", ar = "إقرار السكن أو عقد الإيجar", es = "Declaración de Vivienda", fr = "Déclaration d'Hébergement"), LocalizedText(it = "Contratto di locazione registrato o ospitalità.", en = "Registered rental contract or hospitality form.", ar = "عقد إيجار مسجل أو نموذج إعلان ضيافة.", es = "Contrato de alquiler registrado o formulario de hospitalidad.", fr = "Contrat de location enregistré ou déclaration d'hébergement.")),
                GuideDocument("doc_bollo", LocalizedText(it = "Marca da Bollo €16.00", en = "Revenue Stamp €16.00", ar = "طابع إيرادات بقيمة ١٦ يورو", es = "Sello de Ingresos de €16.00", fr = "Timbre Fiscal de 16,00 €"), LocalizedText(it = "Comprata in tabaccheria da incollare sul modulo 1.", en = "Bought at a tobacconist to be attached to Form 1.", ar = "يتم شراؤه من محلات التبغ (Tabaccheria) ويُلصق على النموذج رقم ١.", es = "Comprado en un estanco para pegar en el Formulario 1.", fr = "Acheté dans un bureau de tabac pour coller sur le formulaire 1."))
            ),
            deadlines = listOf(
                GuideDeadline(
                    LocalizedText(it = "Entro 8 Giorni", en = "Within 8 Days", ar = "خلال ٨ أيام", es = "Dentro de 8 Días", fr = "Sous 8 Jours"),
                    LocalizedText(it = "Invio del kit postale dall'ingresso in Italia.", en = "Submit the postal kit from your entry date in Italy.", ar = "إرسال المغلف البريدي بعد دخول إيطاليا لأول مرة.", es = "Envío del kit postal desde la entrada a Italia.", fr = "Envoi du kit postal à compter de l'entrée en Italie."),
                    "danger"
                ),
                GuideDeadline(
                    LocalizedText(it = "60 Giorni Prima", en = "60 Days Before", ar = "قبل ٦٠ يوماً", es = "60 Días Antes", fr = "60 Jours Avant"),
                    LocalizedText(it = "Richiedi il rinnovo prima della data di scadenza.", en = "Apply for renewal before your current card expires.", ar = "تقديم طلب التجديد قبل تاريخ انتهاء الصلاحية الحالي.", es = "Solicita la renovación antes de la fecha de caducidad.", fr = "Demandez le renouvellement avant la date d'expiration."),
                    "warning"
                )
            ),
            whereToStart = GuideWhereToStart(
                mainOffice = LocalizedText(it = "Poste Italiane (Sportello Amico)", en = "Post Office (Sportello Amico)", ar = "مكتب البريد (Sportello Amico)", es = "Oficina de Correos", fr = "Bureaux de Poste"),
                explanation = LocalizedText(
                    it = "La domanda iniziale si presenta spedendo il kit compilato presso un ufficio postale abilitato Sportello Amico.",
                    en = "The initial application is filed by sending the compiled kit at an authorized Sportello Amico post office.",
                    ar = "يتم تقديم الطلب الأولي عن طريق إرسال المغلف البريدي المكتمل عبر أي مكتب بريد يحتوي على خدمة Sportello Amico.",
                    es = "La solicitud inicial se presenta enviando el kit en una oficina de correos autorizada Sportello Amico.",
                    fr = "La demande initiale est soumise en envoyant le kit postal depuis un bureau de poste agréé Sportello Amico."
                )
            ),
            costBreakdown = listOf(
                GuideCostItem(LocalizedText(it = "Marca da Bollo", en = "Revenue Stamp", ar = "طابع الإيرادات", es = "Sello de Ingresos", fr = "Timbre Fiscal"), "€16.00"),
                GuideCostItem(LocalizedText(it = "Costo Spedizione Kit", en = "Kit Shipping Cost", ar = "تكلفة شحن المغلف", es = "Costo de Envío del Kit", fr = "Frais d'Envoi du Kit"), "€30.00"),
                GuideCostItem(LocalizedText(it = "Costo Permesso Plastificato", en = "Card Print Fee", ar = "رسom طباعة البطاقة", es = "Costo del Permiso Físico", fr = "Frais d'Impression"), "€30.46"),
                GuideCostItem(LocalizedText(it = "Tassa di Soggiorno (1 anno)", en = "Stay Tax (1 year)", ar = "ضريبة الإقامة (سنة واحدة)", es = "Tasa de Residencia (1 año)", fr = "Taxe de Séjour (1 an)"), "€40.00")
            ),
            commonMistakes = listOf(
                GuideCommonMistake(
                    LocalizedText(it = "Mancata firma sul Modulo 1", en = "Missing signature on Form 1", ar = "عدم التوقيع على النموذج رقم ١", es = "Falta de firma en el Formulario 1", fr = "Signature manquante sur le formulaire 1"),
                    LocalizedText(
                        it = "Spedire il kit senza firmare i moduli invalida la domanda causando ritardi.",
                        en = "Sending the kit without signatures invalidates your request, causing massive delays.",
                        ar = "إرسال المغلف دون توقيع النماذج يؤدي إلى إبطال الطلب وتأخيرات هائلة.",
                        es = "Enviar el kit sin firmas invalida la solicitud y causa grandes retrasos.",
                        fr = "L'envoi du kit sans signature invalide la demande et entraîne d'importants retards."
                    )
                ),
                GuideCommonMistake(
                    LocalizedText(it = "Mancanza di tutte le pagine del passaporto", en = "Not copying all passport pages", ar = "عدم نسخ جميع صفحات جواز السفر", es = "No copiar todas las páginas del pasaporte", fr = "Omission de copier toutes les pages du passeport"),
                    LocalizedText(
                        it = "Bisogna fotocopiare TUTTE le pagine, anche quelle bianche senza timbri.",
                        en = "You must photocopy ALL pages of your passport, even empty blank ones.",
                        ar = "يجب تصوير جميع صفحات جواز السفر بالكامل، حتى الصفحات الفارغة غير المختومة.",
                        es = "Debes fotocopiar TODAS las páginas de tu pasaporte, incluidas las que estén en blanco.",
                        fr = "Vous devez photocopier TOUTES les pages de votre passeport, même les pages vierges."
                    )
                )
            ),
            faqs = listOf(
                GuideFaq(
                    LocalizedText(it = "Posso lavorare con la sola ricevuta?", en = "Can I work with only the post receipt?", ar = "هل يمكنني العمل بمجرد الحصول على إيصال البريد؟", es = "¿Puedo trabajar solo con el recibo?", fr = "Puis-je travailler uniquement avec le reçu ?"),
                    LocalizedText(
                        it = "Sì, se si tratta di un rinnovo o se hai un visto che lo consente, puoi continuare a lavorare legalmente.",
                        en = "Yes, if it's a renewal or if you have an entry visa that allows it, you can legally work.",
                        ar = "نعم، إذا كان هذا طلب تجديد أو إذا كان لديك تأشيرة دخول تسمح بذلك، يمكنك العمل بشكل قانوني.",
                        es = "Sí, si se trata de una renovación o si tienes una visa de entrada que lo permita, puedes trabajar legalmente.",
                        fr = "Oui, s'il s'agit d'un renouvellement ou si vous possédez un visa d'entrée l'autorisant, vous pouvez travailler."
                    )
                ),
                GuideFaq(
                    LocalizedText(it = "Posso viaggiare all'estero?", en = "Can I travel abroad?", ar = "هل يمكنني السفر إلى الخارج؟", es = "¿Puedo viajar al extranjero?", fr = "Puis-je voyager à l'étranger ?"),
                    LocalizedText(
                        it = "Puoi viaggiare verso il tuo paese d'origine senza scali in area Schengen, portando passaporto e ricevuta originale.",
                        en = "You can travel to your home country directly (no Schengen layovers) with your passport and original receipt.",
                        ar = "يمكنك السفر إلى بلدك الأصلي مباشرة (بدون توقف ترانزيت في منطقة شنغن) حاملاً جواز السفر والإيصال الأصلي.",
                        es = "Puedes viajar directamente a tu país de origen (sin escalas Schengen) con tu pasaporte y recibo original.",
                        fr = "Vous pouvez voyager directement vers votre pays d'origine (sans escale Schengen) avec passeport et reçu original."
                    )
                )
            ),
            helpfulContacts = listOf(
                GuideContact(LocalizedText(it = "Linea Verde Immigrazione", en = "Immigration Hotline", ar = "الخط الساخن للهجرة", es = "Línea Directa", fr = "Numéro d'Urgence"), "phone", "800309309"),
                GuideContact(LocalizedText(it = "Portale Nazionale", en = "National Portal", ar = "البوابة الوطنية الإلكترونية", es = "Portal Oficial", fr = "Portail Officiel"), "web", "https://www.portaleimmigrazione.it")
            ),
            officialResources = listOf(
                GuideOfficialResource(LocalizedText(it = "Ministero dell'Interno", en = "Ministry of Interior", ar = "وزارة الداخلية الإيطالية", es = "Ministerio del Interior", fr = "Ministère de l'Intérieur"), "https://www.interno.gov.it", "Ministero dell'Interno"),
                GuideOfficialResource(LocalizedText(it = "Polizia di Stato", en = "State Police Portal", ar = "بوابة شرطة الدولة", es = "Policía del Estado", fr = "Police de l'État"), "https://questure.poliziadistato.it/stranieri/", "Questura"),
                GuideOfficialResource(LocalizedText(it = "Poste Italiane - Portale", en = "Poste Italiane Hub", ar = "موقع البريد الإيطالي", es = "Poste Italiane", fr = "Poste Italiane"), "https://www.poste.it", "Poste Italiane")
            ),
            lastReviewed = LocalizedText(it = "12 Luglio 2026", en = "12 July 2026", ar = "١٢ يوليو ٢٠٢٦", es = "12 de Julio de 2026", fr = "12 Juillet 2026"),
            guideVersion = "v1.2",
            lastUpdated = LocalizedText(it = "12 Luglio 2026", en = "12 July 2026", ar = "١٢ يوليو ٢٠٢٦", es = "12 de Julio de 2026", fr = "12 Juillet 2026"),
            provinceNotes = listOf(
                ProvinceNote("Milano", LocalizedText(
                    it = "A Milano, il fotosegnalamento avviene di solito entro 3 mesi presso la Questura di Via Fatebenefratelli.",
                    en = "In Milan, biometrics are usually scheduled within 3 months at the Via Fatebenefratelli Questura.",
                    ar = "في ميلانو، يتم أخذ البصمات عادةً في غضون ٣ أشهر في كويستورا Via Fatebenefratelli.",
                    es = "En Milán, las huellas se programan generalmente en 3 meses en la Questura de Via Fatebenefratelli.",
                    fr = "À Milan, la prise d'empreintes est généralement planifiée sous 3 mois à la Questura Via Fatebenefratelli."
                )),
                ProvinceNote("Roma", LocalizedText(
                    it = "A Roma, a causa dell'alto afflusso, i tempi per l'appuntamento possono estendersi fino a 6-8 mesi.",
                    en = "In Rome, due to high volume, appointments can take up to 6-8 months.",
                    ar = "في روما، بسبب الضغط الشديد، قد تمتد المواعيد إلى ٦-٨ أشهر.",
                    es = "En Roma, debido al gran volumen, las citas pueden tardar hasta 6-8 meses.",
                    fr = "À Rome, en raison du volume important, les rendez-vous peuvent prendre de 6 à 8 mois."
                ))
            ),
            warnings = listOf(
                GuideWarning(
                    severity = "danger",
                    title = LocalizedText(it = "Documenti Reddito Incompleti", en = "Incomplete Income Documents", ar = "وثائق الدخل ناقصة", es = "Documentos de Ingresos Incompletos", fr = "Justificatifs de Revenu Incomplets"),
                    description = LocalizedText(
                        it = "La mancata allegazione dell'Unilav o dell'ultima busta paga è il motivo principale di rifiuto della pratica.",
                        en = "Failure to attach the Unilav contract or your latest payslip is the primary cause of application denial.",
                        ar = "عدم إرفاق نموذج Unilav أو آخر قسيمة راتب هو السبب الرئيسي لرفض المعاملة.",
                        es = "No adjuntar el contrato Unilav o la última nómina es la causa principal de denegación de la solicitud.",
                        fr = "L'absence de contrat Unilav ou de la dernière fiche de paie est le motif principal de rejet."
                    )
                ),
                GuideWarning(
                    severity = "warning",
                    title = LocalizedText(it = "Idoneità Alloggiativa", en = "Housing Certificate Expiry", ar = "صلاحية شهادة السكن", es = "Vencimiento de Idoneidad", fr = "Validité de la Conformité de Logement"),
                    description = LocalizedText(
                        it = "Verifica che il certificato di idoneità alloggiativa non sia scaduto (validità di 6 mesi).",
                        en = "Ensure your housing suitability certificate is not expired (it is valid for 6 months).",
                        ar = "تأكد من أن شهادة ملاءمة السكن ليست منتهية الصلاحية (صالحة لمدة ٦ أشهر فقط).",
                        es = "Asegúrate de que tu certificado de idoneidad de vivienda no haya caducado (es válido por 6 meses).",
                        fr = "Assurez-vous que votre certificat de conformité de logement n'est pas expiré (valide 6 mois)."
                    )
                )
            ),
            communityTips = listOf(
                CommunityTip(
                    id = "tip_work_1",
                    author = LocalizedText(it = "Utente Anonimo", en = "Anonymous User", ar = "مستخدم مجهول", es = "Usuario Anónimo", fr = "Utilisateur Anonyme"),
                    content = LocalizedText(
                        it = "Portate sempre con voi 4 fototessere uguali. A volte in Questura le chiedono anche se le avete caricate.",
                        en = "Always bring 4 identical physical passport photos. Sometimes the Questura asks for them even if they are uploaded.",
                        ar = "أحضر معك دائماً ٤ صور شخصية متطابقة. تطلبها الكويستورا أحياناً حتى لو قمت برفعها الكترونياً.",
                        es = "Lleva siempre 4 fotos de pasaporte físicas idénticas. A veces las piden en la Questura aunque las hayas subido.",
                        fr = "Apportez toujours 4 photos d'identité physiques identiques. La Questura les demande parfois même si elles sont en ligne."
                    ),
                    initialHelpfulCount = 12
                ),
                CommunityTip(
                    id = "tip_work_2",
                    author = LocalizedText(it = "Soggiorno User", en = "Soggiorno User", ar = "مستخدم تطبيقنا", es = "Usuario Soggiorno", fr = "Utilisateur Soggiorno"),
                    content = LocalizedText(
                        it = "Fate una fotocopia di tutto il kit giallo prima di chiuderlo e spedirlo. Vi servirà come riferimento!",
                        en = "Make a photocopy of the entire yellow kit before sealing and sending it. It is extremely helpful as a reference!",
                        ar = "قم بعمل نسخة ضوئية من الملف الأصفر بالكامل قبل إغلاقه وإرساله. ستفيدك كمرجع لاحقاً!",
                        es = "Haz una fotocopia de todo el kit amarillo antes de cerrarlo y enviarlo. ¡Te servirá de referencia!",
                        fr = "Faites une photocopie de tout le kit jaune avant de le fermer et l'envoyer. Très utile comme référence !"
                    ),
                    initialHelpfulCount = 8
                )
            ),
            aiPrompts = listOf(
                LocalizedText(it = "Cosa succede se perdo il lavoro subordinato?", en = "What if I lose my subordinate job?", ar = "ماذا يحدث إذا فقدت عملي التابع؟", es = "¿Qué pasa si pierdo mi trabajo subordinado?", fr = "Que se passe-t-il si je perds mon emploi salarié?"),
                LocalizedText(it = "Come posso provare il reddito minimo richiesto?", en = "How do I prove the required minimum income?", ar = "كيف أثبت الحد الأدنى للدخل المطلوب؟", es = "¿Cómo demuestro el ingreso mínimo requerido?", fr = "Comment prouver le revenu minimum requis?"),
                LocalizedText(it = "Quali documenti servono per l'alloggio idoneo?", en = "What documents are needed for suitable housing?", ar = "ما هي الوثائق المطلوبة للسكن الملائم؟", es = "¿Qué documentos se necesitan para vivienda adecuada?", fr = "Quels documents pour un logement conforme?")
            ),
            estimatedTimelineDetails = GuideEstimatedTimeline(
                averageTime = LocalizedText(it = "3 - 6 Mesi", en = "3 - 6 Months", ar = "٣ - ٦ أشهر", es = "3 - 6 Meses", fr = "3 - 6 Mois"),
                fastTrackOption = LocalizedText(
                    it = "Le conversioni da studio a lavoro subordinato sono elaborate più rapidamente.",
                    en = "Conversions from study to work permits are processed much faster.",
                    ar = "تتم معالجة تحويلات الإقامة من دراسة إلى عمل بشكل أسرع بكثير.",
                    es = "Las conversiones de estudio a trabajo se procesan con mayor rapidez.",
                    fr = "Les conversions d'études à travail salarié sont traitées plus rapidement."
                ),
                delayReasons = listOf(
                    LocalizedText(it = "Mancanza di idoneità alloggiativa aggiornata.", en = "Lack of an updated housing suitability certificate.", ar = "عدم وجود شهادة ملاءمة سكن حديثة.", es = "Falta de certificado de idoneidad de vivienda actualizado.", fr = "Absence de certificat de conformité de logement récent."),
                    LocalizedText(it = "Sovraccarico amministrativo degli uffici della Questura locale.", en = "Administrative overload at local Questura offices.", ar = "الضغط الإداري الشديد في مكاتب الكويستورا المحلية.", es = "Sobrecarga administrativa en las oficinas de la Questura local.", fr = "Surcharge administrative des bureaux de la Questura locale.")
                ),
                relativeSpeedPercent = 0.6f
            ),
            legalReferences = listOf(
                LegalReference(
                    law = LocalizedText(it = "D.Lgs. 286/1998 (Testo Unico Immigrazione)", en = "Legislative Decree No. 286/1998 (Immigration Act)", ar = "المرسوم التشريعي رقم ٢٨٦/١٩٩٨ (قانون الهجرة الموحد)", es = "D.Lgs. 286/1998 (Ley de Extranjería)", fr = "Décret Législatif No. 286/1998 (Loi sur l'Immigration)"),
                    article = LocalizedText(it = "Articolo 5 (Disposizioni generali) e Articolo 22", en = "Article 5 (General provisions) and Article 22", ar = "المادة ٥ (أحكام عامة) والمادة ٢٢", es = "Artículo 5 (Disposiciones generales) y Artículo 22", fr = "Article 5 (Dispositions générales) et Article 22"),
                    circular = LocalizedText(it = "Circolare Ministero Interno n. 40055/2024", en = "Ministry of Interior Circular no. 40055/2024", ar = "منشور وزارة الداخلية رقم ٤٠٠٥٥/٢٠٢٤", es = "Circular del Ministerio del Interior n. 40055/2024", fr = "Circulaire du Ministère de l'Intérieur n° 40055/2024"),
                    explanation = LocalizedText(
                        it = "Disciplina le condizioni di ingresso e soggiorno per motivi di lavoro subordinato, incluso il reddito minimo.",
                        en = "Establishes the conditions of entry and stay for subordinate work purposes, including minimum earnings.",
                        ar = "يحدد شروط الدخول والإقامة لأغراض العمل التابع، بما في ذلك الحد الأدنى للدخل.",
                        es = "Establece las condiciones de entrada y residencia por motivos de trabajo subordinado, incluidos ingresos mínimos.",
                        fr = "Régit les conditions d'entrée et de séjour pour travail salarié, y compris les revenus minimums."
                    )
                )
            ),
            recommendations = listOf("renewal", "long_term")
        ),

        // 2. STUDY PERMIT
        ImmigrationGuide(
            id = "study_permit",
            category = "study",
            iconName = "school",
            title = LocalizedText(
                it = "Permesso di Soggiorno per Studio",
                en = "Study Permit (University/School)",
                ar = "تصريح الدراسة (الجامعة والتعليم)",
                es = "Permiso de Estudio (Universidad/Escuela)",
                fr = "Titre de Séjour pour Études"
            ),
            description = LocalizedText(
                it = "Guida completa per studenti internazionali iscritti a corsi in Italia.",
                en = "Complete guide for international students enrolled in courses in Italy.",
                ar = "دليل كامل للطلاب الدوليين المسجلين في دورات دراسية في إيطاليا.",
                es = "Guía completa para estudiantes internacionales matriculados en cursos en Italia.",
                fr = "Guide complet pour les étudiants internationaux inscrits à des cours en Italie."
            ),
            difficulty = LocalizedText(
                it = "Facile", en = "Easy", ar = "سهل", es = "Fácil", fr = "Facile"
            ),
            estimatedDuration = LocalizedText(
                it = "2 - 4 Mesi", en = "2 - 4 Months", ar = "٢ - ٤ أشهر", es = "2 - 4 Meses", fr = "2 - 4 Mois"
            ),
            estimatedCosts = LocalizedText(
                it = "ca. €120", en = "approx. €120", ar = "حوالي ١٢٠ يورو", es = "aprox. €120", fr = "env. €120"
            ),
            validityPeriod = LocalizedText(
                it = "1 Anno", en = "1 Year", ar = "سنة واحدة", es = "1 Año", fr = "1 An"
            ),
            requiredOffices = listOf("Poste Italiane", "Questura", "Università / Segreteria"),
            overview = GuideOverview(
                whoIsItFor = LocalizedText(
                    it = "Studenti stranieri che entrano in Italia con visto di studio.",
                    en = "Foreign students entering Italy with a study visa.",
                    ar = "الطلاب الأجانب القادمون إلى إيطاليا بتأشيرة دراسية.",
                    es = "Estudiantes extranjeros que ingresan a Italia con visa de estudio.",
                    fr = "Étudiants étrangers entrant en Italie avec un visa d'études."
                ),
                whoCanApply = LocalizedText(
                    it = "Studenti iscritti a università, accademie o scuole professionali riconosciute.",
                    en = "Students registered at recognized universities, academies, or vocational schools.",
                    ar = "الطلاب المسجلون في الجامعات أو الأكاديميات أو المدارس المهنية المعترف بها.",
                    es = "Estudiantes matriculados en universidades, academias o escuelas acreditadas.",
                    fr = "Étudiants inscrits dans des universités, académies ou écoles de formation reconnues."
                ),
                mainRequirements = listOf(
                    LocalizedText(
                        it = "Certificato d'iscrizione al corso o università.",
                        en = "Enrolment certificate from the school or university.",
                        ar = "شهادة القيد والتسجيل في الدورة الدراسية أو الجامعة.",
                        es = "Certificado de matrícula del curso o universidad.",
                        fr = "Certificat d'inscription au cours ou à l'université."
                    ),
                    LocalizedText(
                        it = "Assicurazione sanitaria valida per l'Italia.",
                        en = "Health insurance valid in Italian territory.",
                        ar = "تأمين صحي ساري المفعول في الأراضي الإيطالية.",
                        es = "Seguro médico válido para el territorio italiano.",
                        fr = "Assurance maladie valide sur le territoire italien."
                    ),
                    LocalizedText(
                        it = "Dimostrazione di mezzi economici sussistenza (circa €6.000 all'anno).",
                        en = "Proof of financial support (approx. €6,000 per year).",
                        ar = "إثبات الموارد المالية الكافية للمعيشة (حوالي ٦,٠٠٠ يورو سنوياً).",
                        es = "Prueba de sustento económico (aprox. €6,000 al año).",
                        fr = "Preuve de ressources financières suffisantes (env. 6 000 € par an)."
                    )
                )
            ),
            timeline = listOf(
                GuideTimelineStep(
                    1,
                    LocalizedText(it = "Visto d'Ingresso", en = "Entry Visa", ar = "تأشيرة الدخول", es = "Visa de Entrada", fr = "Visa d'Entrée"),
                    LocalizedText(
                        it = "Ottieni il visto di studio dall'Ambasciata italiana prima di partire.",
                        en = "Obtain your study visa from the Italian Embassy before traveling.",
                        ar = "احصل على تأشيرة الدراسة من السفارة الإيطالية في بلدك قبل السفر.",
                        es = "Obtén la visa de estudio en la Embajada italiana antes de viajar.",
                        fr = "Obtenez votre visa d'études auprès de l'ambassade d'Italie avant votre départ."
                    )
                ),
                GuideTimelineStep(
                    2,
                    LocalizedText(it = "Spedizione Kit Giallo", en = "Postal Kit Send-off", ar = "إرسال ملف البريد الأصفر", es = "Envío de Kit Postal", fr = "Envoi du Kit Postal"),
                    LocalizedText(
                        it = "Invia il kit entro 8 giorni dall'arrivo in Italia inserendo la marca da bollo.",
                        en = "Submit the kit within 8 days of arrival, with copies and a revenue stamp.",
                        ar = "أرسل الملف عبر البريد خلال ٨ أيام من تاريخ الوصول مع إرفاق طابع الإيرادات.",
                        es = "Envía el kit dentro de los 8 días de tu llegada a Italia agregando el sello fiscal.",
                        fr = "Envoyez le kit sous 8 jours après votre arrivée, avec les copies et le timbre fiscal."
                    )
                ),
                GuideTimelineStep(
                    3,
                    LocalizedText(it = "Impronte e Originali", en = "Biometrics", ar = "البصمات والأصول", es = "Biometría y Cita", fr = "Biométrie"),
                    LocalizedText(
                        it = "Fornisci le impronte digitali alla Questura nel giorno del tuo appuntamento.",
                        en = "Provide biometrics at the Questura on the scheduled appointment day.",
                        ar = "قم بإعطاء البصمات الحيوية في مكتب الكويستورا في يوم الموعد المحدد.",
                        es = "Proporciona tus huellas dactilares en la Questura el día programado.",
                        fr = "Présentez-vous pour la prise d'empreintes à la Questura le jour du rendez-vous."
                    )
                )
            ),
            documents = listOf(
                GuideDocument("doc_pass_st", LocalizedText(it = "Passaporto", en = "Passport", ar = "جواز السفر", es = "Pasaporte", fr = "Passeport"), LocalizedText(it = "Fotocopia di tutte le pagine scritte.", en = "Photocopy of all written pages.", ar = "صورة ضوئية لجميع صفحات جواز السفر.", es = "Fotocopia de todas las páginas escritas.", fr = "Photocopie de toutes les pages écrites.")),
                GuideDocument("doc_enroll", LocalizedText(it = "Certificato d'Iscrizione", en = "Enrolment Certificate", ar = "شهادة التسجيل الجامعي", es = "Matrícula Universitaria", fr = "Certificat d'Inscription"), LocalizedText(it = "Rilasciato dall'Università o scuola.", en = "Issued by your University or Course.", ar = "يتم إصداره من سكرتارية الجامعة أو المعهد.", es = "Emitido por la Universidad o centro educativo.", fr = "Délivré par l'Université ou l'établissement d'enseignement.")),
                GuideDocument("doc_insurance", LocalizedText(it = "Assicurazione Sanitaria", en = "Health Insurance", ar = "التأمين الصحي", es = "Seguro Médico", fr = "Assurance Maladie"), LocalizedText(it = "Polizza valida per l'Italia (es. W.A.Y. o iscrizione S.S.N.).", en = "Policy valid for Italy or National Health Service registration.", ar = "وثيقة تأمين صالحة أو إيصال التسجيل في الضمان الصحي الوطني SSN.", es = "Póliza de seguro válida en Italia o registro SSN.", fr = "Police d'assurance valide en Italie ou inscription SSN.")),
                GuideDocument("doc_funds", LocalizedText(it = "Mezzi Economici", en = "Financial Means", ar = "الملاءة المادية", es = "Sustento Financiero", fr = "Ressources Financières"), LocalizedText(it = "Estratto conto bancario o borsa di studio comprovata.", en = "Bank statement showing sufficient funds or proof of scholarship.", ar = "كشف حساب بنكي يثبت وجود المبالغ الكافية أو مستند المنحة الدراسية.", es = "Extracto bancario con fondos suficientes o beca certificada.", fr = "Relevé de compte bancaire ou attestation de bourse d'études."))
            ),
            deadlines = listOf(
                GuideDeadline(
                    LocalizedText(it = "Entro 8 Giorni", en = "Within 8 Days", ar = "خلال ٨ أيام", es = "Dentro de 8 Días", fr = "Sous 8 Jours"),
                    LocalizedText(it = "Invia il kit per non essere considerato irregolare.", en = "Submit your kit to maintain a regular legal status.", ar = "أرسل الملف البريدي فوراً لضمان قانونية الإقامة والوضع القانوني.", es = "Envía tu kit para mantener un estado legal regular.", fr = "Envoyez le kit pour conserver un statut de séjour légal."),
                    "danger"
                )
            ),
            whereToStart = GuideWhereToStart(
                mainOffice = LocalizedText(it = "Ufficio Postale (Amico)", en = "Post Office (Amico)", ar = "مكتب البريد", es = "Oficina de Correos", fr = "Bureau de Poste"),
                explanation = LocalizedText(
                    it = "Spedisci il kit inserendo il bollettino pagato, la marca da bollo ed i documenti dello studente.",
                    en = "Ship the kit including paid slips, revenue stamp, and student documentation.",
                    ar = "أرسل الملف البريدي مرفقاً به إيصال الدفع المدفوع، طابع الإيرادات ومستندات الطالب.",
                    es = "Envía el kit incluyendo el boleto pagado, sello de ingresos y documentos de estudiante.",
                    fr = "Expédiez le kit comprenant le bulletin payé, le timbre fiscal et les pièces de l'étudiant."
                )
            ),
            costBreakdown = listOf(
                GuideCostItem(LocalizedText(it = "Marca da Bollo", en = "Revenue Stamp", ar = "طابع الإيرادات", es = "Sello de Ingresos", fr = "Timbre Fiscal"), "€16.00"),
                GuideCostItem(LocalizedText(it = "Spedizione Kit Giallo", en = "Postal Kit Shipping", ar = "تكلفة شحن البريد", es = "Envío de Correo", fr = "Envoi Postal"), "€30.00"),
                GuideCostItem(LocalizedText(it = "Permesso di Soggiorno Studio", en = "Study Permit fee", ar = "رسوم تصريح الدراسة", es = "Tasa de Permiso", fr = "Taxe de Séjour"), "€70.46")
            ),
            commonMistakes = listOf(
                GuideCommonMistake(
                    LocalizedText(it = "Assicurazione senza copertura adeguata", en = "Insurance without full coverage", ar = "تأمين صحي غير كافٍ التغطية", es = "Seguro sin cobertura adecuada", fr = "Assurance santé insuffisante"),
                    LocalizedText(
                        it = "L'assicurazione sanitaria deve coprire le emergenze e il ricovero ospedaliero per l'intera durata.",
                        en = "Your health insurance must cover urgent care and emergency stays for the entire year.",
                        ar = "يجب أن يغطي التأمين الصحي حالات الطوارئ والإقامة في المستشفيات طوال فترة الإقامة.",
                        es = "Tu seguro médico debe cubrir emergencias y hospitalización para toda la estancia.",
                        fr = "L'assurance doit couvrir les urgences et l'hospitalisation pour toute la durée du séjour."
                    )
                )
            ),
            faqs = listOf(
                GuideFaq(
                    LocalizedText(it = "Posso lavorare con un permesso di studio?", en = "Can I work with a study permit?", ar = "هل يمكنني العمل بتصريح الدراسة؟", es = "¿Puedo trabajar con visa de estudiante?", fr = "Puis-je travailler avec un visa d'études ?"),
                    LocalizedText(
                        it = "Sì, per un massimo di 20 ore settimanali e fino a 1.040 ore annue cumulabili.",
                        en = "Yes, you can legally work for up to 20 hours per week and a maximum of 1,040 hours per year.",
                        ar = "نعم، يمكنك العمل بشكل قانوني بحد أقصى ٢٠ ساعة أسبوعياً و١٠٤٠ ساعة كحد أقصى سنوياً.",
                        es = "Sí, puedes trabajar legalmente un máximo de 20 horas por semana y hasta 1,040 horas al año.",
                        fr = "Oui, vous pouvez travailler légalement jusqu'à 20 heures par semaine (max 1 040 heures par an)."
                    )
                )
            ),
            helpfulContacts = listOf(
                GuideContact(LocalizedText(it = "Servizio Studenti Stranieri", en = "International Student Desk", ar = "مكتب الطلاب الدوليين", es = "Oficina Internacional", fr = "Bureau des Étudiants"), "web", "https://www.universitaly.it")
            ),
            officialResources = listOf(
                GuideOfficialResource(LocalizedText(it = "Universitaly Portal", en = "Universitaly Portal", ar = "بوابة يونيفرسيتالي الرسمية", es = "Portal Universitaly", fr = "Portail Universitaly"), "https://www.universitaly.it", "Ministero dell'Istruzione")
            ),
            lastReviewed = LocalizedText(it = "01 Giugno 2026", en = "01 June 2026", ar = "٠١ يونيو ٢٠٢٦", es = "01 de Junio de 2026", fr = "01 Juin 2026"),
            guideVersion = "v1.1",
            lastUpdated = LocalizedText(it = "01 Giugno 2026", en = "01 June 2026", ar = "٠١ يونيو ٢٠٢٦", es = "01 de Junio de 2026", fr = "01 Juin 2026"),
            provinceNotes = listOf(
                ProvinceNote("Torino", LocalizedText(
                    it = "A Torino, gli studenti universitari possono richiedere un canale prioritario per il rinnovo tramite l'edificio EDISU.",
                    en = "In Turin, university students can apply for a priority renewal channel through the EDISU office.",
                    ar = "في تورينو، يمكن لطلاب الجامعة طلب مسار أولوية للتجديد عبر مكتب EDISU.",
                    es = "En Turín, los estudiantes universitarios pueden solicitar renovación prioritaria vía EDISU.",
                    fr = "À Turin, les étudiants peuvent demander un canal de renouvellement prioritaire via l'EDISU."
                ))
            ),
            warnings = listOf(
                GuideWarning(
                    severity = "danger",
                    title = LocalizedText(it = "Superamento Limiti di Lavoro", en = "Exceeding Job Hours Limits", ar = "تجاوز حد ساعات العمل", es = "Exceso de Horas Laborales", fr = "Dépassement des Heures de Travail"),
                    description = LocalizedText(
                        it = "Lavorare più di 20 ore settimanali viola le condizioni del permesso e può comportare la revoca.",
                        en = "Working more than 20 hours per week violates your stay permit terms and may lead to cancellation.",
                        ar = "العمل لأكثر من ٢٠ ساعة في الأسبوع ينتهك شروط تصريح الدراسة وقد يعرضك لإلغاء الإقامة.",
                        es = "Trabajar más de 20 horas semanales viola las condiciones de la visa y puede causar su revocación.",
                        fr = "Travailler plus de 20 heures par semaine viole les conditions du titre et peut entraîner son retrait."
                    )
                )
            ),
            communityTips = listOf(
                CommunityTip(
                    id = "tip_study_1",
                    author = LocalizedText(it = "Studente Internazionale", en = "International Student", ar = "طالب دولي", es = "Estudiante Internacional", fr = "Étudiant International"),
                    content = LocalizedText(
                        it = "Registratevi sul portale della vostra università per scaricare l'iscrizione ufficiale. Non usate l'autocertificazione!",
                        en = "Sign up on your university portal to download the official enrollment letter. Self-certifications are often rejected!",
                        ar = "سجل في بوابة جامعتك لتحميل مستند القيد الرسمي. شهادات التصريح الذاتي غالباً ما تُرفض!",
                        es = "Inscríbete en el portal universitario para descargar la matrícula oficial. ¡Las declaraciones juradas no sirven!",
                        fr = "Inscrivez-vous sur le portail universitaire pour télécharger l'inscription officielle. Pas d'auto-certification !"
                    ),
                    initialHelpfulCount = 15
                )
            ),
            aiPrompts = listOf(
                LocalizedText(it = "Posso convertire il mio permesso di studio in lavoro?", en = "Can I convert my study permit into a work permit?", ar = "هل يمكنني تحويل إقامتي الدراسية إلى إقامة عمل؟", es = "¿Puedo convertir mi permiso de estudio a trabajo?", fr = "Puis-je convertir mon titre d'études en titre salarié?"),
                LocalizedText(it = "Quali esami servono per rinnovare il permesso?", en = "How many exams do I need to renew my permit?", ar = "كم عدد الامتحانات المطلوبة لتجديد الإقامة؟", es = "¿Cuántos exámenes necesito para renovar el permiso?", fr = "Combien d'examens faut-il pour renouveler le titre?"),
                LocalizedText(it = "Qual è la copertura assicurativa minima?", en = "What is the minimum insurance coverage required?", ar = "ما هو الحد الأدنى للتغطية التأمينية المطلوبة؟", es = "¿Cuál es la cobertura de seguro mínima requerida?", fr = "Quelle est la couverture d'assurance minimale requise?")
            ),
            estimatedTimelineDetails = GuideEstimatedTimeline(
                averageTime = LocalizedText(it = "2 - 4 Mesi", en = "2 - 4 Months", ar = "٢ - ٤ أشهر", es = "2 - 4 Meses", fr = "2 - 4 Mois"),
                fastTrackOption = LocalizedText(
                    it = "Gli uffici Questura per gli studenti universitari sono di solito separati e più veloci.",
                    en = "University student immigration desks are usually dedicated and operate faster.",
                    ar = "عادة ما تكون مكاتب الطلاب في الكويستورا منفصلة وتعمل بوقت أسرع.",
                    es = "Las oficinas de extranjería para estudiantes suelen ser independientes y más rápidas.",
                    fr = "Les guichets étudiants universitaires sont généralement dédiés et plus rapides."
                ),
                delayReasons = listOf(
                    LocalizedText(it = "Assicurazione sanitaria non conforme.", en = "Incompliant health insurance policy.", ar = "وثيقة تأمين صحي غير مطابقة للشروط.", es = "Póliza de seguro de salud no conforme.", fr = "Police d'assurance maladie non conforme."),
                    LocalizedText(it = "Mancato superamento del numero minimo di esami richiesto.", en = "Not having passed the required minimum number of university exams.", ar = "عدم اجتياز الحد الأدنى المطلوب من الامتحانات الجامعية.", es = "No haber aprobado el número mínimo de exámenes requerido.", fr = "Ne pas avoir réussi le nombre minimal d'examens requis.")
                ),
                relativeSpeedPercent = 0.3f
            ),
            legalReferences = listOf(
                LegalReference(
                    law = LocalizedText(it = "D.P.R. 394/1999 (Regolamento di attuazione)", en = "D.P.R. 394/1999 (Implementing Regulation)", ar = "المرسوم الرئاسي رقم ٣٩٤/١٩٩٩ (اللائحة التنفيذية)", es = "D.P.R. 394/1999 (Regolamento de Ejecución)", fr = "D.P.R. 394/1999 (Règlement d'Application)"),
                    article = LocalizedText(it = "Articolo 46 (Soggiorno per motivi di studio)", en = "Article 46 (Stay for study purposes)", ar = "المادة ٤٦ (الإقامة لأغراض الدراسة)", es = "Artículo 46 (Residencia por motivos de estudio)", fr = "Article 46 (Séjour pour motifs d'études)"),
                    circular = LocalizedText(it = "Linee Guida Ministero Università n. 210/2025", en = "Ministry of University Guidelines no. 210/2025", ar = "إرشادات وزارة التعليم العالي والبحث العلمي رقم ٢١٠/٢٠٢٥", es = "Pautas del Ministerio de Universidades n. 210/2025", fr = "Directives du Ministère de l'Enseignement Supérieur n° 210/2025"),
                    explanation = LocalizedText(
                        it = "Definisce le regole per il rinnovo annuale degli studenti e i crediti universitari minimi.",
                        en = "Defines the rules for annual student renewals and the minimum required university credits.",
                        ar = "يحدد قواعد التجديد السنوي للطلاب والحد الأدنى للوحدات الدراسية (الكريديت) المطلوبة.",
                        es = "Define las reglas para la renovación estudiantil anual y los créditos mínimos requeridos.",
                        fr = "Définit les règles de renouvellement annuel des étudiants et les crédits universitaires requis."
                    )
                )
            ),
            recommendations = listOf("work_permit", "renewal")
        ),

        // 3. RENEWAL
        ImmigrationGuide(
            id = "renewal",
            category = "renewal",
            iconName = "refresh",
            title = LocalizedText(
                it = "Guida al Rinnovo del Permesso",
                en = "Stay Permit Renewal Guide",
                ar = "دليل تجديد تصريح الإقامة",
                es = "Guía de Renovación del Permiso",
                fr = "Guide de Renouvellement du Titre"
            ),
            description = LocalizedText(
                it = "Tutto ciò che serve per rinnovare la propria carta prima della scadenza.",
                en = "Everything you need to renew your residency card before expiration.",
                ar = "كل ما تحتاجه لتجديد بطاقة إقامتك بنجاح قبل تاريخ انتهائها.",
                es = "Todo lo que necesitas para renovar tu tarjeta de residencia antes de que venza.",
                fr = "Tout ce dont vous avez besoin pour renouveler votre carte avant son expiration."
            ),
            difficulty = LocalizedText(
                it = "Medio", en = "Medium", ar = "متوسط", es = "Medio", fr = "Moyen"
            ),
            estimatedDuration = LocalizedText(
                it = "3 - 5 Mesi", en = "3 - 5 Months", ar = "٣ - ٥ أشهر", es = "3 - 5 Meses", fr = "3 - 5 Mois"
            ),
            estimatedCosts = LocalizedText(
                it = "ca. €120 - €150", en = "approx. €120 - €150", ar = "حوالي ١٢٠ - ١٥٠ يورو", es = "aprox. €120 - €150", fr = "env. €120 - €150"
            ),
            validityPeriod = LocalizedText(
                it = "1 - 2 Anni", en = "1 - 2 Years", ar = "سنة - سنتين", es = "1 - 2 Años", fr = "1 - 2 Ans"
            ),
            requiredOffices = listOf("Poste Italiane", "Questura"),
            overview = GuideOverview(
                whoIsItFor = LocalizedText(
                    it = "Cittadini stranieri legalmente soggiornanti che devono prorogare la validità del permesso.",
                    en = "Foreign citizens legally residing who must extend the validity of their permit.",
                    ar = "المواطنون الأجانب المقيمون قانونياً الذين يتعين عليهم تمديد صلاحية تصريحهم.",
                    es = "Ciudadanos extranjeros legalmente residentes que deben prorrogar su permiso.",
                    fr = "Citoyens étrangers résidant légalement qui doivent prolonger la validité de leur titre."
                ),
                whoCanApply = LocalizedText(
                    it = "Chi possiede ancora i requisiti iniziali (es. lavoro, studio, famiglia).",
                    en = "Those who still meet the initial requirements (e.g. employment, education, family).",
                    ar = "كل من لا يزال يستوفي الشروط الأساسية الأولى (مثل العمل أو الدراسة أو الأسباب العائلية).",
                    es = "Quienes aún cumplan con los requisitos iniciales (ej. trabajo, estudios, familia).",
                    fr = "Ceux qui remplissent toujours les conditions initiales (par exemple travail, études, famille)."
                ),
                mainRequirements = listOf(
                    LocalizedText(
                        it = "Permesso di soggiorno originale in scadenza.",
                        en = "Original residency permit close to expiration.",
                        ar = "بطاقة تصريح الإقامة الأصلية الموشكة على الانتهاء.",
                        es = "Permiso de residencia original a punto de caducar.",
                        fr = "Titre de séjour original proche de l'expiration."
                    ),
                    LocalizedText(
                        it = "Documentazione aggiornata (nuovi contratti, tasse o esami universitari).",
                        en = "Updated documentation (new contracts, tax slips, or university exams completed).",
                        ar = "المستندات المحدثة بالكامل (عقود جديدة، قسائم ضرائب، أو امتحانات جامعية مجتازة).",
                        es = "Documentación actualizada (nuevos contratos, comprobantes fiscales o exámenes universitarios).",
                        fr = "Documentation mise à jour (nouveau contrat, fiches de paie ou examens universitaires)."
                    )
                )
            ),
            timeline = listOf(
                GuideTimelineStep(
                    1,
                    LocalizedText(it = "Raccolta Documenti", en = "Document Collection", ar = "جمع المستندات", es = "Recopilar Documentos", fr = "Collecte des Documents"),
                    LocalizedText(
                        it = "Raccogli le nuove prove di reddito, alloggio ed iscrizioni.",
                        en = "Gather your new proofs of income, housing registration, and course enrollment.",
                        ar = "اجمع الإثباتات الجديدة للدخل والمأوى وشهادات القيد الدراسي المحدثة.",
                        es = "Reúne tus nuevas pruebas de ingresos, habitabilidad y matrículas.",
                        fr = "Rassemblez les nouvelles preuves de revenus, de logement et d'inscription."
                    )
                ),
                GuideTimelineStep(
                    2,
                    LocalizedText(it = "Invio del Kit", en = "Submission", ar = "إرسال الملف", es = "Envío de Kit", fr = "Envoi du Kit"),
                    LocalizedText(
                        it = "Invia il kit giallo compilando il Modulo 1 ed eventuale Modulo 2.",
                        en = "Submit the yellow kit by completing Form 1 and eventually Form 2.",
                        ar = "أرسل الملف الأصفر البريدي مع ملء النموذج رقم ١ والنموذج رقم ٢ عند الاقتضاء.",
                        es = "Envía el kit completando el Formulario 1 y eventualmente el Formulario 2.",
                        fr = "Soumettez le kit jaune en remplissant le formulaire 1 et éventuellement le formulaire 2."
                    )
                )
            ),
            documents = listOf(
                GuideDocument("doc_old_permesso", LocalizedText(it = "Permesso in Scadenza", en = "Expiring Permit", ar = "الإقامة الموشكة على الانتهاء", es = "Permiso por Vencer", fr = "Permis Expiring"), LocalizedText(it = "Copia del permesso fronte-retro.", en = "Copy of front and back of your current permit card.", ar = "نسخة من وجه وخلف البطاقة الحالية.", es = "Copia de ambas caras del permiso actual.", fr = "Copie recto-verso de votre titre de séjour actuel.")),
                GuideDocument("doc_fiscal", LocalizedText(it = "Codice Fiscale", en = "Tax Code (Codice Fiscale)", ar = "الرقم الضريبي", es = "Código Fiscal", fr = "Numéro Fiscal"), LocalizedText(it = "Copia della tessera sanitaria o foglio ufficiale.", en = "Copy of your health card or official tax code paper.", ar = "نسخة من البطاقة الصحية أو الورقة الرسمية الصادرة من مصلحة الضرائب.", es = "Copia de la tarjeta sanitaria o documento oficial del código fiscal.", fr = "Copie de la carte vitale ou du document officiel du code fiscal.")),
                GuideDocument("doc_income_re", LocalizedText(it = "Dichiarazione dei Redditi", en = "Income Statement", ar = "إثبات الدخل السنui", es = "Declaración de Ingresos", fr = "Justificatif de Revenus"), LocalizedText(it = "Modello 730, Unico o ultime tre buste paga.", en = "Tax returns Form 730, Unico, or the last three payslips.", ar = "الإقرار الضريبي السنوي المعتمد أو آخر ثلاث قسائم للراتب.", es = "Declaración fiscal Modelo 730, Único o tres últimas nóminas.", fr = "Déclaration fiscale Modèle 730, Unico ou trois dernières fiches de paie."))
            ),
            deadlines = listOf(
                GuideDeadline(
                    LocalizedText(it = "60 Giorni Prima", en = "60 Days Before", ar = "قبل ٦٠ يوماً", es = "60 Días Antes", fr = "60 Jours Avant"),
                    LocalizedText(it = "Consigliato iniziare il rinnovo entro questa data.", en = "Recommended window to apply for renewal to prevent status lapses.", ar = "الفترة الموصى بها للبدء بإجراءات التجديد لتلافي انتهاء صلاحية إقامتك.", es = "Plazo recomendado para iniciar el trámite antes del vencimiento.", fr = "Délai recommandé pour entamer le renouvellement afin d'éviter l'expiration."),
                    "warning"
                )
            ),
            whereToStart = GuideWhereToStart(
                mainOffice = LocalizedText(it = "Ufficio Postale", en = "Post Office", ar = "مكتب البريد", es = "Oficina de Correos", fr = "Bureau de Poste"),
                explanation = LocalizedText(
                    it = "Usa il kit per richiedere il rinnovo allegando la copia del vecchio permesso.",
                    en = "Fill out and mail the yellow kit containing a copy of your old card.",
                    ar = "املأ وأرسل المغلف البريدي الأصفر مرفقاً به نسخة من إقامتك القديمة المنتهية.",
                    es = "Completa y envía el kit incluyendo una copia de tu tarjeta de residencia anterior.",
                    fr = "Remplissez et envoyez le kit jaune avec une copie de votre ancien titre."
                )
            ),
            costBreakdown = listOf(
                GuideCostItem(LocalizedText(it = "Marca da Bollo", en = "Revenue Stamp", ar = "طابع الإيرادات", es = "Sello de Ingresos", fr = "Timbre Fiscal"), "€16.00"),
                GuideCostItem(LocalizedText(it = "Spedizione Kit", en = "Kit Shipping", ar = "تكلفة شحن الملف", es = "Costo de Envío", fr = "Frais d'Envoi"), "€30.00"),
                GuideCostItem(LocalizedText(it = "Tassa di Soggiorno", en = "Stay Tax", ar = "ضريبة الإقامة", es = "Tasa de Residencia", fr = "Taxe de Séjour"), "€40.00 - €80.00")
            ),
            commonMistakes = listOf(
                GuideCommonMistake(
                    LocalizedText(it = "Richiesta tardiva oltre i termini", en = "Late renewal beyond limits", ar = "التأخر الشديد في تقديم الطلب", es = "Renovación tardía fuera de término", fr = "Demande tardive hors délais"),
                    LocalizedText(
                        it = "Presentare la domanda dopo 60 giorni dalla scadenza può rendere irregolari ed esposti ad espulsione.",
                        en = "Filing more than 60 days after expiration makes you illegal and subject to expulsion.",
                        ar = "تقديم طلب التجديد بعد فوات ٦٠ يوماً على تاريخ الانتهاء يعرضك لإلغاء الإقامة والترحيل.",
                        es = "Presentar la solicitud más de 60 días después de vencido puede generar irregularidad y expulsión.",
                        fr = "Le dépôt de la demande plus de 60 jours après l'expiration peut vous rendre irrégulier."
                    )
                )
            ),
            faqs = listOf(
                GuideFaq(
                    LocalizedText(it = "Cosa succede se perdo la ricevuta?", en = "What if I lose my post receipt?", ar = "ماذا يحدث إذا فقدت إيصال البريد؟", es = "¿Qué pasa si pierdo el recibo?", fr = "Que faire si je perds mon reçu ?"),
                    LocalizedText(
                        it = "Devi sporgere immediata denuncia di smarrimento alle Forze dell'Ordine e richiederne un duplicato.",
                        en = "You must immediately file a loss report with the police and request a duplicate receipt.",
                        ar = "يتعين عليك التوجه فوراً لإجراء بلاغ فقدان لدى أقرب قسم شرطة وطلب نسخة بديلة رسمية.",
                        es = "Debes presentar una denuncia de pérdida ante la policía y solicitar un duplicado.",
                        fr = "Vous devez immédiatement déclarer la perte à la police et demander un duplicat."
                    )
                )
            ),
            helpfulContacts = listOf(
                GuideContact(LocalizedText(it = "Sito Portale", en = "Portal Website", ar = "موقع البوابة", es = "Sitio Web", fr = "Site Web"), "web", "https://www.portaleimmigrazione.it")
            ),
            officialResources = listOf(
                GuideOfficialResource(LocalizedText(it = "Portale Immigrazione", en = "Portale Immigrazione", ar = "بوابة الهجرة الإيطالية", es = "Portale Immigrazione", fr = "Portale Immigrazione"), "https://www.portaleimmigrazione.it", "Poste Italiane")
            ),
            lastReviewed = LocalizedText(it = "15 Maggio 2026", en = "15 May 2026", ar = "١٥ مايو ٢٠٢٦", es = "15 de Mayo de 2026", fr = "15 Mai 2026"),
            guideVersion = "v1.0",
            lastUpdated = LocalizedText(it = "15 Maggio 2026", en = "15 May 2026", ar = "١٥ مايو ٢٠٢٦", es = "15 de Mayo de 2026", fr = "15 Mai 2026"),
            provinceNotes = listOf(
                ProvinceNote("Napoli", LocalizedText(
                    it = "A Napoli, gli uffici postali consigliano di presentarsi il mercoledì per sbrigare più velocemente la spedizione dei kit gialli.",
                    en = "In Naples, post offices suggest arriving on Wednesdays for faster processing of yellow kits.",
                    ar = "في نابولي، تنصح مكاتب البريد بالذهاب يوم الأربعاء لإنهاء شحن المجلد الأصفر بشكل أسرع.",
                    es = "En Nápoles, las oficinas de correos aconsejan ir los miércoles para un trámite más rápido.",
                    fr = "À Naples, les bureaux de poste conseillent de venir le mercredi pour un traitement plus rapide."
                ))
            ),
            warnings = listOf(
                GuideWarning(
                    severity = "warning",
                    title = LocalizedText(it = "Termini di Scadenza Rigidi", en = "Strict Expiration Terms", ar = "شروط صارمة لتاريخ الانتهاء", es = "Plazos de Vencimiento Estrictos", fr = "Délais d'Expiration Stricts"),
                    description = LocalizedText(
                        it = "La legge prevede l'avvio della pratica di rinnovo prima della scadenza. Non aspettare l'ultimo giorno!",
                        en = "The law requires starting the renewal procedure before expiration. Do not wait until the last day!",
                        ar = "يفرض القانون البدء في إجراءات التجديد قبل تاريخ الانتهاء. لا تنتظر حتى اليوم الأخير!",
                        es = "La ley exige iniciar el trámite de renovación antes del vencimiento. ¡No esperes al último día!",
                        fr = "La loi exige d'entamer le renouvellement avant l'expiration. N'attendez pas le dernier jour !"
                    )
                )
            ),
            communityTips = listOf(
                CommunityTip(
                    id = "tip_renewal_1",
                    author = LocalizedText(it = "User Esperto", en = "Expert User", ar = "مستخدم خبير", es = "Usuario Experto", fr = "Utilisateur Expérimenté"),
                    content = LocalizedText(
                        it = "Conservate sempre la ricevuta postale (cedolino)! È l'unico documento che attesta il vostro soggiorno legale in Italia durante l'attesa.",
                        en = "Always keep your postal receipt safe! It is the only document that proves your legal stay in Italy during the wait.",
                        ar = "حافظ دائماً على إيصال البريد (Cedolino) بأمان! هو المستند الوحيد الذي يثبت قانونية إقامتك في إيطاليا أثناء الانتظار.",
                        es = "¡Guarda siempre el recibo postal! Es il único documento que certifica tu estancia legal en Italia durante la espera.",
                        fr = "Conservez précieusement le reçu postal ! C'est le seul document prouvant votre séjour légal pendant l'attente."
                    ),
                    initialHelpfulCount = 34
                )
            ),
            aiPrompts = listOf(
                LocalizedText(it = "Posso viaggiare nei paesi Schengen con la ricevuta?", en = "Can I travel to Schengen countries with the receipt?", ar = "هل يمكنني السفر إلى دول شنغن بالإيصال البريدي؟", es = "¿Puedo viajar a países Schengen con el recibo?", fr = "Puis-je voyager dans l'espace Schengen avec le reçu?"),
                LocalizedText(it = "Cosa fare se la mia vecchia carta è già scaduta?", en = "What if my old card has already expired?", ar = "ماذا أفعل إذا كانت بطاقتي القديمة منتهية بالفعل؟", es = "¿Qué hago si mi tarjeta anterior ya venció?", fr = "Que faire si mon ancien titre est déjà expiré?"),
                LocalizedText(it = "La ricevuta postale vale come documento d'identità?", en = "Is the post receipt valid as an ID?", ar = "هل يعتبر إيصال البريد مستند إثبات شخصية؟", es = "¿El recibo postal sirve como documento de identidad?", fr = "Le reçu postal vaut-il pièce d'identité?")
            ),
            estimatedTimelineDetails = GuideEstimatedTimeline(
                averageTime = LocalizedText(it = "3 - 5 Mesi", en = "3 - 5 Months", ar = "٣ - ٥ أشهر", es = "3 - 5 Meses", fr = "3 - 5 Mois"),
                fastTrackOption = LocalizedText(
                    it = "I rinnovi legati a motivi familiari o cure mediche urgenti vengono trattati con priorità.",
                    en = "Renewals linked to family reasons or urgent medical treatments are handled with high priority.",
                    ar = "تتم معالجة التجديدات المتعلقة بالأسباب العائلية أو الرعاية الطبية الطارئة بأولوية قصوى.",
                    es = "Las renovaciones por motivos familiares o tratamientos médicos urgentes tienen prioridad.",
                    fr = "Les renouvellements pour motifs familiaux ou soins urgents sont traités en priorité."
                ),
                delayReasons = listOf(
                    LocalizedText(it = "Mancata dimostrazione della continuità del reddito.", en = "Lack of proof regarding continuous income flow.", ar = "عدم القدرة على إثبات استمرارية الدخل المالي.", es = "Falta de pruebas de continuidad de ingresos.", fr = "Absence de justificatif de continuité des revenus."),
                    LocalizedText(it = "Mancata risposta a eventuali raccomandate inviate dalla Questura.", en = "Failing to reply to registered letters sent by the Questura.", ar = "عدم الرد على الخطابات المسجلة المرسلة من الكويستورا.", es = "No responder a las cartas certificadas enviadas por la Questura.", fr = "Absence de réponse aux courriers recommandés de la Questura.")
                ),
                relativeSpeedPercent = 0.5f
            ),
            legalReferences = listOf(
                LegalReference(
                    law = LocalizedText(it = "D.P.R. 394/1999 (Regolamento di attuazione)", en = "D.P.R. 394/1999 (Stay Regulation)", ar = "المرسوم الرئاسي رقم ٣٩٤/١٩٩٩", es = "D.P.R. 394/1999", fr = "D.P.R. 394/1999"),
                    article = LocalizedText(it = "Articolo 11 e Articolo 12", en = "Article 11 and Article 12", ar = "المادة ١١ والمادة ١٢", es = "Artículo 11 y Artículo 12", fr = "Article 11 et Article 12"),
                    circular = LocalizedText(it = "Circolare n. 400/A/2023 della Polizia di Stato", en = "State Police Circular no. 400/A/2023", ar = "منشور شرطة الدولة رقم ٤٠٠/أ/٢٠٢٣", es = "Circular n. 400/A/2023 de la Policía de Estado", fr = "Circulaire n° 400/A/2023 de la Police de l'État"),
                    explanation = LocalizedText(
                        it = "Stabilisce le modalità di rinnovo, i termini per la presentazione e l'efficacia legale della ricevuta postale.",
                        en = "Establishes renewal modalities, submission terms, and the legal value of the postal receipt.",
                        ar = "يحدد طرق التجديد، والمواعيد النهائية لتقديم الطلب، والأثر القانوني لإيصال البريد.",
                        es = "Establece las modalidades de renovación, plazos de presentación y valor legal del recibo.",
                        fr = "Fixe les modalités de renouvellement, les délais et la valeur légale du reçu postal."
                    )
                )
            ),
            recommendations = listOf("work_permit", "long_term")
        ),

        // 4. LONG-TERM PERMIT (SOGGIORNO ILLIMITATO)
        ImmigrationGuide(
            id = "long_term",
            category = "long_term",
            iconName = "star",
            title = LocalizedText(
                it = "Permesso di Soggiorno UE Lungo Periodo",
                en = "EU Long-Term Residence Permit (Unlimited)",
                ar = "تصريح الإقامة طويل الأجل غير المحدود UE",
                es = "Permiso de Larga Duración UE (Ilimitado)",
                fr = "Carte de Résident UE de Longue Durée"
            ),
            description = LocalizedText(
                it = "Guida al permesso di soggiorno a tempo indeterminato (ex Carta di Soggiorno).",
                en = "Guide to obtaining the permanent residency permit in Italy (ex Carta di Soggiorno).",
                ar = "دليل الحصول على تصريح الإقامة الدائمة غير المحدد الصلاحية (كرت الإقامة سابقاً).",
                es = "Guía para obtener el permiso de residencia permanente (ex Carta de Soggiorno).",
                fr = "Guide pour obtenir la carte de séjour permanent en Italie (ex Carta di Soggiorno)."
            ),
            difficulty = LocalizedText(
                it = "Difficile", en = "Hard", ar = "صعب", es = "Difícil", fr = "Difficile"
            ),
            estimatedDuration = LocalizedText(
                it = "4 - 8 Mesi", en = "4 - 8 Months", ar = "٤ - ٨ أشهر", es = "4 - 8 Meses", fr = "4 - 8 Mois"
            ),
            estimatedCosts = LocalizedText(
                it = "ca. €200", en = "approx. €200", ar = "حوالي ٢٠٠ يورو", es = "aprox. €200", fr = "env. €200"
            ),
            validityPeriod = LocalizedText(
                it = "Illimitata (Aggiornamento ogni 10 anni)", en = "Unlimited (Update every 10 years)", ar = "غير محدود الصلاحية (تحديث كل ١٠ سنوات)", es = "Ilimitado (Actualización cada 10 años)", fr = "Illimitée (Mise à jour tous les 10 ans)"
            ),
            requiredOffices = listOf("Poste Italiane", "Questura", "Centro per l'Impiego / INPS"),
            overview = GuideOverview(
                whoIsItFor = LocalizedText(
                    it = "Stranieri regolarmente residenti da almeno 5 anni in Italia.",
                    en = "Foreigners legally residing in Italy for at least 5 consecutive years.",
                    ar = "الأجانب المقيمون بصفة قانونية ومستمرة في إيطاليا لمدة لا تقل عن ٥ سنوات.",
                    es = "Extranjeros legalmente residentes en Italia durante al menos 5 años.",
                    fr = "Étrangers résidant légalement en Italie depuis au moins 5 ans."
                ),
                whoCanApply = LocalizedText(
                    it = "Candidati con reddito stabile superiore all'importo annuo dell'assegno sociale, alloggio idoneo e test di lingua italiana superato.",
                    en = "Applicants with stable income above the social allowance limit, suitable housing, and certified Italian language proficiency.",
                    ar = "المتقدمون الذين يتمتعون بدخل مالي مستقر يفوق الحد السنوي لإعانة الضمان، ومسكن ملائم، واجتياز اختبار اللغة الإيطالية.",
                    es = "Solicitantes con ingresos estables superiores al subsidio social, vivienda adecuada y examen de idioma italiano aprobado.",
                    fr = "Candidats disposant d'un revenu stable, d'un logement conforme et d'un certificat de maîtrise de la langue italienne."
                ),
                mainRequirements = listOf(
                    LocalizedText(
                        it = "Soggiorno continuativo in Italia per almeno 5 anni.",
                        en = "Continuous and legal residency in Italy for at least 5 years.",
                        ar = "الإقامة المستمرة والقانونية في إيطاليا لمدة ٥ سنوات على الأقل.",
                        es = "Residencia continua y legal en Italia durante al menos 5 años.",
                        fr = "Résidence continue et légale en Italie pendant au moins 5 ans."
                    ),
                    LocalizedText(
                        it = "Certificazione di Italiano di livello minimo A2.",
                        en = "Certified knowledge of Italian language (minimum level A2).",
                        ar = "شهادة إتقان اللغة الإيطالية بمستوى A2 كحد أدنى.",
                        es = "Certificación de conocimiento del idioma italiano (nivel mínimo A2).",
                        fr = "Certification de connaissance de la langue italienne (niveau minimum A2)."
                    ),
                    LocalizedText(
                        it = "Certificato del Casellario Giudiziale e Carichi Pendenti privo di reati ostativi.",
                        en = "No pending criminal charges or record of severe offenses.",
                        ar = "سجل جنائي خالٍ تماماً من الجرائم المانعة للحصول على الإقامة.",
                        es = "Certificado de antecedentes penales libre de delitos graves.",
                        fr = "Casier judiciaire vierge de toute infraction grave."
                    )
                )
            ),
            timeline = listOf(
                GuideTimelineStep(
                    1,
                    LocalizedText(it = "Esame di Italiano", en = "Language Test", ar = "اختبار اللغة الإيطالية", es = "Examen de Idioma", fr = "Test de Langue"),
                    LocalizedText(
                        it = "Sostieni l'esame di lingua italiana A2 (se non sei esente o diplomato in Italia).",
                        en = "Take and pass the A2 level Italian exam (unless exempt or graduated in Italy).",
                        ar = "اجتز اختبار اللغة الإيطالية بمستوى A2 (ما لم تكن معفياً أو حاصلاً على شهادة دراسية إيطالية).",
                        es = "Realiza y aprueba el examen de italiano nivel A2 (excepto exentos o graduados en Italia).",
                        fr = "Passez et réussissez l'examen d'italien niveau A2 (sauf dispense ou diplôme obtenu en Italie)."
                    )
                ),
                GuideTimelineStep(
                    2,
                    LocalizedText(it = "Richiesta Documenti Certificati", en = "Certificates Request", ar = "طلب الشهادات الرسمية", es = "Solicitud de Certificados", fr = "Demande de Certificats"),
                    LocalizedText(
                        it = "Richiedi il casellario giudiziale e l'idoneità alloggiativa ufficiale.",
                        en = "Request your criminal records check and the housing suitability certificate.",
                        ar = "اطلب كشف السجل العدلي (الفيش والتشبيه) الرسمي وشهادة ملاءمة السكن المعتمدة.",
                        es = "Solicita el certificado de antecedentes penales y la idoneidad habitacional.",
                        fr = "Demandez l'extrait de casier judiciaire et le certificat de conformité du logement."
                    )
                ),
                GuideTimelineStep(
                    3,
                    LocalizedText(it = "Invio Domanda", en = "Application Sending", ar = "تقديم وإرسال الطلب", es = "Envío de Solicitud", fr = "Envoi de la Demande"),
                    LocalizedText(
                        it = "Invia il Kit Giallo compilato alle Poste allegando la marca da bollo.",
                        en = "Send the completed yellow kit at the post office attaching your receipts and stamps.",
                        ar = "أرسل المغلف البريدي الأصفر المكتمل عبر البريد مع إلحاق طابع الإيرادات المناسب.",
                        es = "Envía el kit amarillo en correos adjuntando los recibos y el sello.",
                        fr = "Envoyez le kit jaune complété à la poste avec les reçus et le timbre fiscal."
                    )
                )
            ),
            documents = listOf(
                GuideDocument("doc_residence_history", LocalizedText(it = "Certificato Storico di Residenza", en = "Historical Residence Certificate", ar = "شهادة السكن التاريخية", es = "Certificado de Residencia Histórico", fr = "Certificat Historique de Résidence"), LocalizedText(it = "Dimostra l'iscrizione anagrafica continuativa negli ultimi 5 anni.", en = "Proves continuous city registry enrollment for 5 years.", ar = "تثبت التسجيل المتواصل في السجل المدني للبلدية طوال السنوات الخمس الماضية.", es = "Prueba el registro continuo en el padrón municipal durante 5 años.", fr = "Prouve l'inscription continue au registre de la commune pendant 5 ans.")),
                GuideDocument("doc_a2_cert", LocalizedText(it = "Attestazione Lingua A2", en = "Italian Language Certificate A2", ar = "شهادة اللغة الإيطالية A2", es = "Certificación de Italiano A2", fr = "Attestation de Langue Italienne A2"), LocalizedText(it = "Rilasciato da enti certificati o scuola statale.", en = "Issued by accredited institutions or public state schools.", ar = "تصدر من مراكز معتمدة رسمياً أو من مدارس حكومية.", es = "Emitido por centros acreditados o escuelas estatales públicas.", fr = "Délivrée par des organismes agréés ou des établissements publics.")),
                GuideDocument("doc_casellario", LocalizedText(it = "Casellario Giudiziale", en = "Criminal Records", ar = "السجل القضائي العدلي", es = "Antecedentes Penales", fr = "Casier Judiciaire"), LocalizedText(it = "Richiesto presso la Procura della Repubblica.", en = "To be requested at the local Prosecutor's office (Procura).", ar = "يتم طلبه من النيابة العامة بالمحكمة المختصة.", es = "A solicitar en la oficina de la Fiscalía (Procura).", fr = "À demander auprès du bureau du Procureur de la République.")),
                GuideDocument("doc_idoneita", LocalizedText(it = "Idoneità Alloggiativa", en = "Housing Suitability", ar = "شهادة ملاءمة السكن الصحي", es = "Idoneidad de Vivienda", fr = "Conformité du Logement"), LocalizedText(it = "Rilasciata dal Comune o dall'ASL competente.", en = "Issued by your Municipality (Comune) or local health authority.", ar = "تصدر من بلدية السكن أو السلطة الصحية المحلية ASL.", es = "Emitido por el Ayuntamiento (Comune) o la autoridad sanitaria local.", fr = "Délivré par la mairie (Comune) ou l'autorité sanitaire locale."))
            ),
            deadlines = listOf(
                GuideDeadline(
                    LocalizedText(it = "Tutto l'Anno", en = "All Year Round", ar = "طوال العام", es = "Todo el Año", fr = "Toute l'Année"),
                    LocalizedText(it = "Può essere richiesto in qualsiasi momento dopo aver compiuto i 5 anni.", en = "Can be requested at any moment once the 5-year requirement is reached.", ar = "يمكن تقديمه والطلب في أي وقت فور إكمال ٥ سنوات من الإقامة القانونية.", es = "Se puede solicitar en cualquier momento tras cumplir los 5 años.", fr = "Peut être demandé à tout moment dès que la condition des 5 ans est remplie."),
                    "info"
                )
            ),
            whereToStart = GuideWhereToStart(
                mainOffice = LocalizedText(it = "Poste Italiane / Patronato", en = "Post Office / Patronato Office", ar = "مكتب البريد أو مكاتب المساعدة (Patronato)", es = "Correos o Patronato", fr = "Poste ou Organisme Patronato"),
                explanation = LocalizedText(
                    it = "Puoi farti aiutare gratuitamente da un Patronato per compilare e verificare l'intera domanda.",
                    en = "You can get free assistance from a Patronato office to draft and verify your complex request.",
                    ar = "يمكنك الحصول على مساعدة مجانية من أي مكتب مساعدات (Patronato) لملء وتدقيق ملف الطلب المعقد.",
                    es = "Puedes recibir asistencia gratuita de un Patronato para completar y verificar tu solicitud.",
                    fr = "Vous pouvez obtenir une assistance gratuite d'un Patronato pour remplir et vérifier votre demande."
                )
            ),
            costBreakdown = listOf(
                GuideCostItem(LocalizedText(it = "Marca da Bollo", en = "Revenue Stamp", ar = "طابع الإيرادات", es = "Sello de Ingresos", fr = "Timbre Fiscal"), "€16.00"),
                GuideCostItem(LocalizedText(it = "Invio Raccomandata Kit", en = "Kit Postage", ar = "شحن المغلف البريدي", es = "Franqueo de Kit", fr = "Frais d'Envoi"), "€30.00"),
                GuideCostItem(LocalizedText(it = "Costo Bollettino Plastificato", en = "Card Printing", ar = "رسوم كرت الإقامة", es = "Boleto de Tarjeta", fr = "Frais de Carte"), "€30.46"),
                GuideCostItem(LocalizedText(it = "Tassa di Soggiorno UE", en = "EU Residence stay tax", ar = "ضريبة الإقامة الأوروبية", es = "Tasa de Residencia UE", fr = "Taxe de Séjour UE"), "€100.00")
            ),
            commonMistakes = listOf(
                GuideCommonMistake(
                    LocalizedText(it = "Conteggio errato dei giorni fuori Italia", en = "Wrong count of days spent abroad", ar = "حساب خاطئ لأيام الغياب عن إيطاليا", es = "Cómputo erróneo de días fuera", fr = "Calcul erroné des jours hors d'Italie"),
                    LocalizedText(
                        it = "Non devi esserti assentato per più di 6 mesi consecutivi o per più di 10 mesi complessivi nei 5 anni.",
                        en = "You must not have been outside Italy for over 6 consecutive months or over 10 months total in 5 years.",
                        ar = "يجب ألا تتجاوز غياباتك خارج إيطاليا ٦ أشهر متتالية أو ١٠ أشهر إجمالاً خلال السنوات الخمس الأخيرة.",
                        es = "No debes haber estado fuera de Italia más de 6 meses seguidos o más de 10 meses en total en los 5 años.",
                        fr = "Vous ne devez pas avoir été absent plus de 6 mois consécutifs ou 10 mois au total sur les 5 ans."
                    )
                )
            ),
            faqs = listOf(
                GuideFaq(
                    LocalizedText(it = "Questo permesso scade?", en = "Does this permit expire?", ar = "هل تنتهي صلاحية هذا التصريح؟", es = "¿Caduca este permiso?", fr = "Ce permis expire-t-il ?"),
                    LocalizedText(
                        it = "No, ha validità illimitata. Devi solo aggiornare la foto e i dati personali ogni 10 anni.",
                        en = "No, it is permanent. You only need to refresh your photo and details every 10 years.",
                        ar = "لا، هو إقامة دائمة غير محدودة الصلاحية. يجب فقط تحديث الصورة والبيانات الشخصية كل ١٠ سنوات.",
                        es = "No, tiene validez ilimitada. Solo debes actualizar la foto y los datos cada 10 años.",
                        fr = "Non, sa validité est illimitée. Vous devez uniquement actualiser la photo et les données tous les 10 ans."
                    )
                )
            ),
            helpfulContacts = listOf(
                GuideContact(LocalizedText(it = "Prenotazione Esame Italiano", en = "Italian Language Test", ar = "حجز اختبار اللغة", es = "Reserva de Examen", fr = "Inscription Test d'Italien"), "web", "https://testitaliano.interno.it")
            ),
            officialResources = listOf(
                GuideOfficialResource(LocalizedText(it = "Polizia di Stato - Soggiorno UE", en = "Polizia di Stato - Stay UE", ar = "موقع الشرطة - الإقامة الأوروبية", es = "Polizia di Stato - Residencia UE", fr = "Polizia di Stato - Séjour UE"), "https://questure.poliziadistato.it", "Questura"),
                GuideOfficialResource(LocalizedText(it = "INPS - Portale", en = "INPS Portal", ar = "بوابة INPS الرسمية", es = "INPS", fr = "INPS"), "https://www.inps.it", "INPS")
            ),
            lastReviewed = LocalizedText(it = "28 Giugno 2026", en = "28 June 2026", ar = "٢٨ يونيو ٢٠٢٦", es = "28 de Junio de 2026", fr = "28 Juin 2026"),
            guideVersion = "v2.0",
            lastUpdated = LocalizedText(it = "28 Giugno 2026", en = "28 June 2026", ar = "٢٨ يونيو ٢٠٢٦", es = "28 de Junio de 2026", fr = "28 Juin 2026"),
            provinceNotes = listOf(
                ProvinceNote("Bologna", LocalizedText(
                    it = "A Bologna, la verifica del reddito INPS integrata viene effettuata d'ufficio tramite convenzioni telematiche.",
                    en = "In Bologna, INPS income verification is automated via digital database exchange.",
                    ar = "في بولونيا، يتم التحقق من دخل INPS تلقائياً من خلال الربط الإلكتروني بين الدوائر.",
                    es = "En Bolonia, la verificación de ingresos de INPS se realiza de oficio por vía digital.",
                    fr = "À Bologne, la vérification des revenus de l'INPS est automatisée en interne."
                ))
            ),
            warnings = listOf(
                GuideWarning(
                    severity = "danger",
                    title = LocalizedText(it = "Casellario Giudiziale Sporco", en = "Active Criminal Records", ar = "وجود سوابق قضائية", es = "Antecedentes Penales Activos", fr = "Antécédents Judiciaires"),
                    description = LocalizedText(
                        it = "Anche reati di lieve entità o passati possono determinare il rifiuto insindacabile di questo permesso definitivo.",
                        en = "Even minor or old convictions can lead to irrevocable denial of this permanent stay permit.",
                        ar = "حتى الإدانات البسيطة أو القديمة قد تؤدي إلى رفض غير قابل للطعن للحصول على هذا التصريح الدائم.",
                        es = "Incluso las condenas menores o antiguas pueden dar lugar a la denegación irrevocable de este permiso permanente.",
                        fr = "Même des condamnations mineures ou anciennes peuvent entraîner le rejet irrévocable de ce titre permanent."
                    )
                )
            ),
            communityTips = listOf(
                CommunityTip(
                    id = "tip_long_1",
                    author = LocalizedText(it = "Residente Storico", en = "Long-term Resident", ar = "مقيم قديم", es = "Residente de Larga Trayectoria", fr = "Résident de Longue Date"),
                    content = LocalizedText(
                        it = "Se avete figli nati in Italia, potete allegare il loro certificato di nascita per dimostrare la stabilità del nucleo familiare.",
                        en = "If you have children born in Italy, attach their birth certificates to prove your family's integration and stability.",
                        ar = "إذا كان لديك أطفال ولدوا في إitalya، يمكنك إرفاق شهادات ميلادهم لإثبات استقرار وتكامل الأسرة.",
                        es = "Si tienes hijos nacidos en Italia, adjunta sus actas de nacimiento para demostrar la estabilidad de tu familia.",
                        fr = "Si vous avez des enfants nés en Italie, joignez leurs actes de naissance pour prouver la stabilité de la famille."
                    ),
                    initialHelpfulCount = 42
                )
            ),
            aiPrompts = listOf(
                LocalizedText(it = "Come funziona il test di italiano A2?", en = "How does the A2 Italian test work?", ar = "كيف يعمل اختبار اللغة الإيطالية A2؟", es = "¿Cómo funciona el examen de italiano A2?", fr = "Comment fonctionne le test d'italien A2?"),
                LocalizedText(it = "Posso richiedere questo permesso se sono disoccupato?", en = "Can I apply for this permit if I am unemployed?", ar = "هل يمكنني التقدم بطلب للحصول على هذا التصريح إذا كنت عاطلاً عن العمل؟", es = "¿Puedo solicitar este permiso si estoy desempleado?", fr = "Puis-je demander ce titre si je suis au chômage?"),
                LocalizedText(it = "Qual è l'importo minimo dell'assegno sociale?", en = "What is the minimum social allowance amount?", ar = "ما هو الحد الأدنى لمبلغ الضمان الاجتماعي؟", es = "¿Cuál es el monto mínimo del subsidio social?", fr = "Quel est le montant minimal de l'allocation sociale?")
            ),
            estimatedTimelineDetails = GuideEstimatedTimeline(
                averageTime = LocalizedText(it = "4 - 8 Mesi", en = "4 - 8 Months", ar = "٤ - ٨ أشهر", es = "4 - 8 Meses", fr = "4 - 8 Mois"),
                fastTrackOption = LocalizedText(
                    it = "I titolari di Carta Blu UE (lavoratori altamente qualificati) hanno tempi di attesa ridotti della metà.",
                    en = "EU Blue Card holders (highly skilled workers) enjoy waiting times cut in half.",
                    ar = "يتمتع حاملو البطاقة الزرقاء الأوروبية (العمال ذوو المهارات العالية) بأوقات انتظار مقسمة إلى النصف.",
                    es = "Los titulares de la Tarjeta Azul UE (trabajadores altamente cualificados) tienen tiempos reducidos a la mitad.",
                    fr = "Les titulaires de Carte Bleue Européenne (travailleurs qualifiés) voient leurs délais réduits de moitié."
                ),
                delayReasons = listOf(
                    LocalizedText(it = "Mancata corrispondenza del nucleo familiare dichiarato con l'alloggio.", en = "Discrepancies between declared family members and housing certificate parameters.", ar = "عدم تطابق عدد أفراد الأسرة المصرح بهم مع مواصفات شهادة السكن.", es = "Falta de coincidencia entre los familiares declarados y el espacio de la vivienda.", fr = "Divergence entre le nombre de membres de la famille déclarés et la taille du logement."),
                    LocalizedText(it = "Ritardi nel rilascio del Casellario Giudiziale da parte della Procura.", en = "Delays in criminal record retrieval from the local Procura office.", ar = "التأخر في إصدار السجل القضائي العدلي من قبل النيابة العامة.", es = "Retrasos en la entrega de antecedentes penales por parte de la Fiscalía.", fr = "Retards de délivrance du casier judiciaire par le parquet.")
                ),
                relativeSpeedPercent = 0.8f
            ),
            legalReferences = listOf(
                LegalReference(
                    law = LocalizedText(it = "D.Lgs. 286/1998 (Testo Unico Immigrazione)", en = "Legislative Decree No. 286/1998", ar = "المرسوم التشريعي رقم ٢٨٦/١٩٩٨", es = "D.Lgs. 286/1998", fr = "Décret Législatif No. 286/1998"),
                    article = LocalizedText(it = "Articolo 9 (Permesso di soggiorno UE per soggiornanti di lungo periodo)", en = "Article 9 (EU Stay permit for long-term residents)", ar = "المادة ٩ (تصريح الإقامة طويل الأجل للإقامة الأوروبية)", es = "Artículo 9 (Permiso de residencia UE de larga duración)", fr = "Article 9 (Titre de séjour de longue durée de l'UE)"),
                    circular = LocalizedText(it = "Direttiva Europea 2003/109/CE e s.m.i.", en = "European Directive 2003/109/EC as amended", ar = "التوجيه الأوروبي ٢٠٠٣/١٠٩/CE والتعديلات اللاحقة", es = "Directiva Europea 2003/109/CE y modif.", fr = "Directive Européenne 2003/109/CE modifiée"),
                    explanation = LocalizedText(
                        it = "Recepisce la normativa europea per la concessione del soggiorno permanente ai cittadini non UE dopo 5 anni.",
                        en = "Transposes European standards on permanent stay authorization to non-EU nationals after 5 years.",
                        ar = "تطبق المعايير الأوروبية لمنح تصريح الإقامة الدائمة لمواطني الدول غير الأعضاء في الاتحاد بعد ٥ سنوات.",
                        es = "Aplica la normativa europea para la concesión de residencia permanente tras 5 años.",
                        fr = "Transpose les normes européennes pour l'octroi du séjour permanent après 5 ans d'habitation."
                    )
                )
            ),
            recommendations = listOf("renewal", "work_permit")
        )
    )
}
