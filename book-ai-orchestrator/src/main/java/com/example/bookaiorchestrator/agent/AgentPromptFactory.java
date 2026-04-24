package com.example.bookaiorchestrator.agent;

import org.springframework.stereotype.Component;

@Component
public class AgentPromptFactory {

    public String buildPrompt(AgentRole role, String input) {
        String prompt = switch (role) {
            case SPEC_AGENT -> specPrompt(input);
            case DEVELOPER_AGENT -> developerPrompt(input);
            case TEST_AGENT -> testPrompt(input);
            case REVIEWER_AGENT -> reviewerPrompt(input);
            case FIX_AGENT -> fixPrompt(input);
        };

        return prompt + "\n" + jsonResponseRules();
    }

    private String specPrompt(String input) {
        return """
                Eres SPEC_AGENT.

                Responsabilidad:
                - Convierte la tarea del usuario en una mini spec SDD.
                - No escribas codigo.
                - La mini spec debe ir resumida en el campo summary.
                - Mantenlo simple.
                - No propongas base de datos, seguridad ni arquitectura hexagonal.

                Tarea:
                %s
                """.formatted(input);
    }

    private String developerPrompt(String input) {
        return """
                Eres DEVELOPER_AGENT.

                Responsabilidad:
                - Recibes una spec o tarea.
                - Devuelve cambios reales de archivos en el JSON.
                - Crea solo un controller simple.
                - El codigo debe ser minimo.
                - No agregues capas extra.
                - No uses base de datos, seguridad ni arquitectura hexagonal.

                Debes responder SIEMPRE con un fileChanges CREATE similar a:
                {
                  "status": "OK",
                  "summary": "Created basic controller",
                  "fileChanges": [
                    {
                      "action": "CREATE",
                      "path": "src/main/java/com/example/book/BookController.java",
                      "content": "package com.example.book;\\n\\nimport org.springframework.web.bind.annotation.PostMapping;\\nimport org.springframework.web.bind.annotation.RequestBody;\\nimport org.springframework.web.bind.annotation.RequestMapping;\\nimport org.springframework.web.bind.annotation.RestController;\\n\\n@RestController\\n@RequestMapping(\\"/books\\")\\npublic class BookController {\\n\\n    @PostMapping(\\"/reserve\\")\\n    public String reserve(@RequestBody ReserveBookRequest request) {\\n        return \\"Book reserved: \\" + request.title() + \\" for \\" + request.userName();\\n    }\\n\\n    public record ReserveBookRequest(String title, String userName) {\\n    }\\n}\\n"
                    }
                  ],
                  "nextAction": "CONTINUE"
                }

                Entrada:
                %s
                """.formatted(input);
    }

    private String testPrompt(String input) {
        return """
                Eres TEST_AGENT.

                Responsabilidad:
                - Recibes una spec, una implementacion propuesta o una tarea.
                - Propon tests minimos con JUnit 5 y MockMvc.
                - No escribas archivos reales.
                - Cubre solo el comportamiento principal.
                - Mantenlo simple.

                Usa fileChanges para indicar archivos de test y codigo sugerido.
                Si no hay cambios de archivo, usa fileChanges como array vacio.

                Entrada:
                %s
                """.formatted(input);
    }

    private String reviewerPrompt(String input) {
        return """
                Eres REVIEWER_AGENT.

                Responsabilidad:
                - Revisa si la implementacion cumple la spec o tarea.
                - Detecta sobreingenieria.
                - Responde OK si no ves problemas.
                - Responde ISSUES si hay problemas concretos.
                - No propongas base de datos, seguridad ni arquitectura hexagonal.

                Usa status OK si no ves problemas.
                Usa status ISSUES si hay problemas concretos.
                Resume los hallazgos en summary.

                Entrada:
                %s
                """.formatted(input);
    }

    private String fixPrompt(String input) {
        return """
                Eres FIX_AGENT.

                Responsabilidad:
                - Recibes issues de revision o una tarea.
                - Propon correcciones minimas.
                - No escribas archivos reales.
                - No agregues complejidad.
                - No uses base de datos, seguridad ni arquitectura hexagonal.

                Usa fileChanges para indicar correcciones minimas si aplican.
                Si no hay cambios de archivo, usa fileChanges como array vacio.

                Entrada:
                %s
                """.formatted(input);
    }

    private String jsonResponseRules() {
        return """

                Responde SIEMPRE con JSON valido.
                No incluyas Markdown fuera del JSON.
                No envuelvas la respuesta en ```json.
                Usa exactamente esta estructura:
                {
                  "status": "OK",
                  "summary": "Short explanation",
                  "fileChanges": [
                    {
                      "action": "CREATE",
                      "path": "src/main/java/example/BookController.java",
                      "content": "file content here"
                    }
                  ],
                  "nextAction": "CONTINUE"
                }

                Reglas:
                - status puede ser OK o ISSUES.
                - fileChanges debe ser [] si no propones cambios.
                - action puede ser CREATE, MODIFY, DELETE o NONE.
                - nextAction puede ser CONTINUE, FIX_REQUIRED o DONE.
                """;
    }
}
