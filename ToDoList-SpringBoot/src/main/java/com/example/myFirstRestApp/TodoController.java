package com.example.myFirstRestApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/todo")
    public ResponseEntity<Todo> get(@RequestParam(value = "id") int id){
        //get todo from db by id
        Optional<Todo> todoInDb = todoRepository.findById(id);
        if(todoInDb.isPresent())
            return new ResponseEntity<Todo>(todoInDb.get(),HttpStatus.OK);
        return new ResponseEntity("No todo found with id " + id, HttpStatus.NOT_FOUND);
    }

    /**
     * es handelt sich um Authentifizierung mit @param secret
     * @param secret
     * @return
     */
    @GetMapping("/todo/all")
    public ResponseEntity<Iterable<Todo>> getAll(@RequestHeader("api-secret") String secret){
        System.out.println(secret);
        var userBySecret = userRepository.findBySecret(secret);

        if(userBySecret.isPresent()){
            Iterable<Todo> allTodosInDb = todoRepository.findAllByUserId(userBySecret.get().getId());
            return new ResponseEntity<Iterable<Todo>>(allTodosInDb,HttpStatus.OK);
        }
        return new ResponseEntity("Invalid api secret", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/todo")
    public ResponseEntity<Todo> create(@RequestBody Todo newTodo){
        //save todo in db
        todoRepository.save(newTodo);
        return new ResponseEntity<Todo>(newTodo,HttpStatus.OK);
    }

    @DeleteMapping("/todo")
    public ResponseEntity delete(@RequestParam(value = "id") int id){
        Optional<Todo> todoInDb = todoRepository.findById(id);

        if(todoInDb.isPresent()) {
            todoRepository.deleteById(id);
            return new ResponseEntity("Todo deleted", HttpStatus.OK);
        }
        return new ResponseEntity("No Todo to delete found with id "+ id, HttpStatus.NOT_FOUND);
    }
}
