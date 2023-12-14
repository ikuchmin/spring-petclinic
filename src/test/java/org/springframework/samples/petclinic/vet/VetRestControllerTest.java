package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link VetRestController}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class VetRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {

	}

	@Test
	@DisplayName("Test find all")
	public void findAllTest() throws Exception {
		mockMvc.perform(get("/rest/vet?include=mainBalance&page[number]=1&page[size]=10&sort=-createdAt"))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("Test find all next")
	public void findAllTestNext() throws Exception {
		mockMvc.perform(get("/rest/vet?include=mainBalance&page%5Bnumber%5D=1&page%5Bsize%5D=10&sort=-createdAt"))
			.andExpect(status().isOk())
			.andDo(print());
	}
}
