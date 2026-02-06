package com.raki.pos.product;

import com.raki.pos.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getActiveProductsForBusiness(Long businessId) {
        return repo.findActiveByBusiness(businessId);
    }

    public Product getProduct(Long id) {
        return repo.findById(id);
    }

    public Long createOrUpdate(Product p) {
        Long productId;
        if (p.getProductId() == null) {
            productId = repo.insert(p);
        } else {
            repo.update(p);
            productId = p.getProductId();
        }

        repo.deleteIngredientsByProduct(productId);
        if (p.getIngredientIds() != null && !p.getIngredientIds().isEmpty()) {
            repo.insertProductIngredients(productId, p.getIngredientIds());
        }

        return productId;
    }

    public void delete(Long productId, Long businessId) {
        repo.delete(productId, businessId);
    }
}
