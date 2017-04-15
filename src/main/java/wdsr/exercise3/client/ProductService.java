package wdsr.exercise3.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import wdsr.exercise3.model.Product;
import wdsr.exercise3.model.ProductType;
import wdsr.exercise3.server.IServerApplication;
import wdsr.exercise3.server.ServerApplication;

public class ProductService extends RestClientBase {

	protected ProductService(final String serverHost, final int serverPort, final Client client) {
		super(serverHost, serverPort, client);
	}

	// private final WebTarget webtarget = baseTarget;

	/**
	 * Looks up all products of given types known to the server.
	 * 
	 * @param types
	 *            Set of types to be looked up
	 * @return A list of found products - possibly empty, never null.
	 */
	public List<Product> retrieveProducts(Set<ProductType> types) {

		Response response = baseTarget.path("/products").queryParam("type", types.toArray())
				.request(MediaType.APPLICATION_JSON).get(Response.class);

		
		return response.readEntity(new GenericType<ArrayList<Product>>() {
		});
	}

	/**
	 * Looks up all products known to the server.
	 * 
	 * @return A list of all products - possibly empty, never null.
	 */
	public List<Product> retrieveAllProducts() {

		
		Response response = baseTarget.path("/products").request(MediaType.APPLICATION_JSON)
				.get(Response.class);
		
		return response.readEntity(new GenericType<ArrayList<Product>>() {
		});

	}

	/**
	 * Looks up the product for given ID on the server.
	 * 
	 * @param id
	 *            Product ID assigned by the server
	 * @return Product if found
	 * @throws NotFoundException
	 *             if no product found for the given ID.
	 */
	public Product retrieveProduct(int id) {

		
		Response response = baseTarget.path("/products/" + id).request(MediaType.APPLICATION_JSON)
				.get(Response.class);

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			throw new NotFoundException();
		}

		return response.readEntity(Product.class);
	}

	/**
	 * Creates a new product on the server.
	 * 
	 * @param product
	 *            Product to be created. Must have null ID field.
	 * @return ID of the new product.
	 * @throws WebApplicationException
	 *             if request to the server failed
	 */

	public int storeNewProduct(Product product) {


		Response response = baseTarget.path("/products").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(product, MediaType.APPLICATION_JSON_TYPE), Response.class);

		if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
			/*
			response.close();
			Response response2 = baseTarget.path("/products").queryParam("name", product.getName())
					.queryParam("type", product.getType()).request(MediaType.APPLICATION_JSON).get(Response.class);

			List<Product> products = response2.readEntity(new GenericType<ArrayList<Product>>() {
			});
			return products.get(products.size() - 1).getId();
			*/
			//nie wiem czemu to nie chce dzia³ac
			return response.readEntity(Product.class).getId();

		} else {
			throw new WebApplicationException();
		}

	}

	/**
	 * Updates the given product.
	 * 
	 * @param product
	 *            Product with updated values. Its ID must identify an existing
	 *            resource.
	 * @throws NotFoundException
	 *             if no product found for the given ID.
	 */
	public void updateProduct(Product product) {
		

		Response response = baseTarget.path("/products/" + product.getId())
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(product, MediaType.APPLICATION_JSON_TYPE), Response.class);

		if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
			throw new NotFoundException();
		}
	}

	/**
	 * Deletes the given product.
	 * 
	 * @param product
	 *            Product to be deleted. Its ID must identify an existing
	 *            resource.
	 * @throws NotFoundException
	 *             if no product found for the given ID.
	 */
	public void deleteProduct(Product product) {

		Response response = baseTarget.path("/products/" + product.getId())
				.request(MediaType.APPLICATION_JSON )
				.delete(Response.class);
		
		if(response.getStatus() == Response.Status.NOT_FOUND.getStatusCode())
		{
			throw new NotFoundException();
		}
		
	}
}
