package com.codecrumbs.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecrumbs.demo.dto.ProgressoQuizCreateDTO;
import com.codecrumbs.demo.dto.QuizComPerguntasDTO;
import com.codecrumbs.demo.dto.QuizComProgressoDTO;
import com.codecrumbs.demo.dto.QuizCreateDTO;
import com.codecrumbs.demo.model.ProgressoQuizModel;
import com.codecrumbs.demo.model.QuizModel;
import com.codecrumbs.demo.service.QuizService;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin("*")
@RequestMapping("quizzes")
public class QuizController {
    
    @Autowired
    private QuizService quizService;

    @GetMapping 
    public ResponseEntity<List<QuizComPerguntasDTO>> getAllQuizzes(){
        List<QuizComPerguntasDTO> lista_dto = quizService.getAllQuizzes();

        return ResponseEntity.status(200).body(lista_dto);
    }

    /**
     * Essa rota traz uma lista de QuizComProgressoDTO, isto é:
     * - Cada DTO tem os atributos do quiz, um atributo que diz se o usuário concluiu o quiz e a nota dele
     * 
     * Na rota, deve ser passado o id do usuario como referencia, e a pagina que vai ser carregada
     * Está sendo feito uma paginação, onde cada pagina vai ter 16 quizzes
     * 
     */
    @GetMapping("todos-quizzes/ref/{id_usuario}/lang/{linguagem}/pagina/{page}")
    public ResponseEntity<List<QuizComProgressoDTO>> getAllQuizzesComProgressoDoUsuario(@PathVariable("id_usuario") Integer id_usuario,
                                                                                        @PathVariable("linguagem") String linguagem,
                                                                                        @PathVariable("page") Integer page){
        List<QuizComProgressoDTO> lista = quizService.getAllQuizzesComProgressoDoUsuario(id_usuario, linguagem, page);                                                
        return ResponseEntity.status(200).body(lista);
    }

    @GetMapping("meus-quizzes/ref/{id_usuario}/lang/{linguagem}/pagina/{page}")
    public ResponseEntity<List<QuizComProgressoDTO>> getMeusQuizzesComProgresso(@PathVariable("id_usuario") Integer id_usuario,
                                                                                        @PathVariable("linguagem") String linguagem,
                                                                                        @PathVariable("page") Integer page){
        List<QuizComProgressoDTO> lista = quizService.getMeusQuizzesComProgresso(id_usuario, linguagem, page);                                                
        return ResponseEntity.status(200).body(lista);
    }

    @GetMapping("{id_quiz}") 
    public ResponseEntity<QuizComPerguntasDTO> getQuizComPerguntas(@PathVariable("id_quiz") Integer id_quiz){
        Optional<QuizComPerguntasDTO> dto = quizService.getQuizComPerguntas(id_quiz);
        
        if(dto.isPresent()){
            return ResponseEntity.status(200).body(dto.get());
        }

        return ResponseEntity.status(400).body(null);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<QuizModel> criarNovoQuiz(@RequestBody QuizCreateDTO dto){
        QuizModel quiz_criado = quizService.criarNovoQuiz(dto);

        return ResponseEntity.status(201).body(quiz_criado);
    }

    @Transactional
    @PostMapping("/progresso/salvar")
    public ResponseEntity<ProgressoQuizModel> salvarProgresso(@RequestBody ProgressoQuizCreateDTO dto){
        Optional<ProgressoQuizModel> optProgresso = quizService.salvarProgressoQuiz(dto);

        if(optProgresso.isPresent()){
            return ResponseEntity.status(201).body(optProgresso.get());
        }

        return ResponseEntity.status(303).body(optProgresso.get());
    }


}
