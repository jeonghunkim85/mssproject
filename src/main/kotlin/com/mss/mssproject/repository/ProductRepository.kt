package com.mss.mssproject.repository

import com.mss.mssproject.domain.Brand
import com.mss.mssproject.domain.Category
import com.mss.mssproject.domain.Product
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ProductRepository: CrudRepository<Product, Long> {

    fun countAllByBrandId(brandId: Long): Long

    @Query("""
        select p 
        from Product p join
            ( select 
                a.id as id,
                row_number() over(partition by a.category order by a.price, a.id desc) as min_rank
            from Product a) z on (p.id = z.id)
        where z.min_rank = 1
        order by p.category.id asc
    """)
    fun findCheapestProductsByAllCategory(): List<Product>

    @Query("""
        select p 
        from Product p join
            (select 
                a.id as id,
                row_number() over(partition by a.brand, a.category order by a.price, a.id desc) as min_rank
            from Product a) z on (p.id = z.id)
        where z.min_rank = 1 
        and p.brand = :brand
    """)
    fun findCheapestProductsByBrand(brand: Brand): List<Product>

    @Query("""
        select
            NEW kotlin.Pair(
                case when r.min_rank = 1 then 'MIN' 
                    when r.max_rank = 1 then 'MAX'
                    else 'UNKNOWN' end,
                p
            )
        from Product p join
        (select 
            a.id as id,
            dense_rank() over(partition by a.category order by a.price) as min_rank,
            dense_rank() over(partition by a.category order by a.price desc) as max_rank
        from Product a) r on (p.id = r.id)
        where
            p.category = :category
            and (r.min_rank = 1 or r.max_rank = 1)
    """)
    fun findCheapestAndMostExpensiveProductsByCategory(category: Category): List<Pair<String, Product>>
}