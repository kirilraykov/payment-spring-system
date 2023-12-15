package com.kraykov.emerchantapp.payment.controller;

import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.service.impl.MerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class MerchantControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private MerchantController merchantController;

    @Mock
    private MerchantService merchantService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = standaloneSetup(merchantController).build();
    }

    @Test
    public void testGetAllMerchants() throws Exception {
        Merchant merchant1 = new Merchant();
        Merchant merchant2 = new Merchant();
        List<Merchant> merchants = Arrays.asList(merchant1, merchant2);
        when(merchantService.getAllMerchants()).thenReturn(merchants);

        mockMvc.perform(get("/api/merchants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(merchant1.getId()))
                .andExpect(jsonPath("$[1].id").value(merchant2.getId()));
    }

    @Test
    public void testGetAllMerchantsEmptyList() throws Exception {
        when(merchantService.getAllMerchants()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/merchants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
