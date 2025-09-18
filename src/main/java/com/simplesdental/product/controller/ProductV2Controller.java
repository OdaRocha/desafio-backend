package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ProductV2DTO;
import com.simplesdental.product.service.ProductService;
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
@RequestMapping("/api/v2/products")
@Tag(name = "Api de Produtos v2", description = "API para gerenciamento de produtos versão 2")
public class ProductV2Controller {

    private final ProductService productService;

    @Autowired
    public ProductV2Controller(ProductService productService) {
        this.productService = productService;
    }


    @Operation(method = "GET", summary = "Lista todos os produtos", description = "Retorna uma lista com todos os produtos cadastrados")
    @ApiResponse(
            responseCode = "200",
            description = "Produto encontrada",
            content =
            @Content(
                    schema = @Schema(implementation = ProductV2DTO.class),
                    examples = @ExampleObject(
                            value = """
                                    [
                                        {
                                            "id": 1,
                                            "code": 99,
                                            "name": "Playstation 5",
                                            "description": "Video game console",
                                            "price": 4.999,00,
                                            "active": true
                                        }
                                    ]
                                    """
                    )
            )
    )

    @GetMapping(produces = "application/json")
    public Page<ProductV2DTO> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return productService.findAllV2(pageable);
    }


    @Operation(method = "GET", summary = "Busca um produto pelo ID", description = "Retorna os detalhes de um produto específico pelo seu ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado",
                    content = @Content(
                            schema = @Schema(implementation = ProductV2DTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "code": 99,
                                                "name": "Playstation 5",
                                                "description": "Video game console",
                                                "price": 4.999,00,
                                                "active": true
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ProductV2DTO> getProductById(
            @Parameter(description = "ID do produto a ser buscado", required = true)
            @PathVariable Long id
    ) {
        return productService.findByIdV2(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(method = "POST", summary = "Cria um novo produto", description = "Cria um novo produto com os detalhes fornecidos")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto criado com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = ProductV2DTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "code": 99,
                                                "name": "Playstation 5",
                                                "description": "Video game console",
                                                "price": 4.999,00,
                                                "active": true
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
                                                "attemptedValue": "PLaystation 5PLaystation 5PLaystation 5PLaystation 5PLaystation 5PLaystation 5PLaystation 5PLaystati",
                                                "code": "BAD_REQUEST"
                                            }
                                            """
                            )
                    )
            ),
    })
    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductV2DTO createProduct(@Valid @RequestBody ProductV2DTO product) {
        return productService.save(product);
    }


    @Operation(method = "PUT", summary = "Atualiza um produto existente", description = "Atualiza os detalhes de um produto existente pelo seu ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto editado com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = ProductV2DTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "code": 99,
                                                "name": "Playstation 5",
                                                "description": "Video game console",
                                                "price": 4.999,00,
                                                "active": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de edicao do produto estao inválidos",
                    content = @Content(
                            schema = @Schema(implementation = ProductV2DTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Nome deve ter menos que 100 caracteres",
                                                "field": "name",
                                                "attemptedValue": "PLaystation 5PLaystation 5PLaystation 5PLaystation 5PLaystation 5PLaystation 5PLaystation 5PLaystati",
                                                "code": "BAD_REQUEST"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ProductV2DTO> updateProduct(
            @Parameter(description = "ID do produto a ser alterado", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductV2DTO product
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @Operation(method = "DELETE", summary = "Remove um produto", description = "Remove um produto existente pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> {
                    productService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}