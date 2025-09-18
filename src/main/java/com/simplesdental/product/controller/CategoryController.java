package com.simplesdental.product.controller;

import com.simplesdental.product.dto.CategoryDTO;
import com.simplesdental.product.dto.ProductV2DTO;
import com.simplesdental.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Api de Categorias de produto", description = "Essa api é responsável por gerenciar as categorias dos produtos")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(method = "GET", summary = "Lista todas as categorias dos produtos", description = "Retorna uma lista com todas as categorias cadastradas")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria encontrado",
                    content = @Content(
                            schema = @Schema(implementation = CategoryDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {
                                                     "id": 1,
                                                     "name": "Eletrônicos",
                                                     "description": "Produtos eletrônicos e gadgets"
                                                 }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrado")
    })
    @GetMapping(produces = "application/json")
    public Page<CategoryDTO> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryService.findAll(pageable);
    }

    @Operation(method = "GET", summary = "Retorna uma categoria", description = "Retorna uma categoria específica pelo seu ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria encontrado",
                    content = @Content(
                            schema = @Schema(implementation = CategoryDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                 "id": 1,
                                                 "name": "Eletrônicos",
                                                 "description": "Produtos eletrônicos e gadgets"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria criada com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = CategoryDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "name": "Eletrônicos",
                                                "description": "Produtos eletrônicos e gadgets"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de criação do produto estao inválidos",
                    content = @Content(
                            schema = @Schema(implementation = ProductV2DTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Nome deve ter menos que 100 caracteres",
                                                "field": "name",
                                                "attemptedValue": "Eletrônicos EletrônicosEletrônicosEletrônicosEletrônicosEletrônicosEletrônicosEletrônicosEletrônicosEletrônicos",
                                                "code": "BAD_REQUEST"
                                            }
                                            """
                            )
                    )
            ),
    })
    @Operation(method = "POST", summary = "Cria uma nova categoria", description = "Cria uma categoria com os detalhes fornecidos")
    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@Valid @RequestBody CategoryDTO category) {
        return categoryService.save(category);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria editada com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = CategoryDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "name": "Eletrônicos",
                                                "description": "Produtos eletrônicos e gadgets"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de edicao de categoria estao inválidos",
                    content = @Content(
                            schema = @Schema(implementation = ProductV2DTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Nome deve ter menos que 100 caracteres",
                                                "field": "name",
                                                "attemptedValue": "Eletrônicos EletrônicosEletrônicosEletrônicosEletrônicosEletrônicosEletrônicosEletrônicosEletrônicosEletrônicos",
                                                "code": "BAD_REQUEST"
                                            }
                                            """
                            )
                    )
            ),
    })
    @Operation(method = "PUT", summary = "Atualiza uma categoria", description = "Atualiza os detalhes de uma categoria existente pelo seu ID")
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "ID da categoria a ser alterada", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO category
    ) {
        return ResponseEntity.ok(categoryService.update(id, category));
    }

    @Operation(method = "DELETE", summary = "Remove uma categoria", description = "Remove uma Categoria existente pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID da categoria a ser removida", required = true)
            @PathVariable Long id
    ) {
        categoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}