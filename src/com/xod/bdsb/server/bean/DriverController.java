package com.xod.bdsb.server.bean;

import com.xod.bdsb.server.dao.DriverDao;
import com.xod.bdsb.server.dto.DriverDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverDao driverDao;

    @RequestMapping(method = RequestMethod.GET)
    public List<DriverDto> getAll() {
        return driverDao.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<DriverDto> getById(@PathVariable Integer id) {
        DriverDto d = driverDao.findById(id);
        if (d == null) {
            return new ResponseEntity<DriverDto>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<DriverDto>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "/search/firstName", method = RequestMethod.GET)
    public List<DriverDto> findByFirstName(@RequestParam("value") String firstName) {
        return driverDao.findByFirstName(firstName);
    }

    @RequestMapping(value = "/search/lastName", method = RequestMethod.GET)
    public List<DriverDto> findByLastName(@RequestParam("value") String lastName) {
        return driverDao.findByLastName(lastName);
    }

    @RequestMapping(value = "/search/yearOfBirth", method = RequestMethod.GET)
    public List<DriverDto> findByYearOfBirth(@RequestParam("value") Integer yearOfBirth) {
        return driverDao.findByYearOfBirth(yearOfBirth);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DriverDto> create(@RequestBody DriverDto driver) {
        Integer id = driverDao.create(driver);
        driver.setId(id);
        return new ResponseEntity<DriverDto>(driver, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<DriverDto> update(@PathVariable Integer id, @RequestBody DriverDto driver) {
        if (driverDao.findById(id) == null) {
            return new ResponseEntity<DriverDto>(HttpStatus.NOT_FOUND);
        }
        driver.setId(id);
        DriverDto updated = driverDao.update(driver);
        return new ResponseEntity<DriverDto>(updated, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (driverDao.findById(id) == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        driverDao.delete(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
