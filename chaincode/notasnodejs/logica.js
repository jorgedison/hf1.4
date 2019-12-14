'use strict';
const { Contract} = require('fabric-contract-api');

class testContract extends Contract {

   async consultaNotas(ctx,studentId) {
   
    let marksAsBytes = await ctx.stub.getState(studentId); 
    if (!marksAsBytes || marksAsBytes.toString().length <= 0) {
      throw new Error('El estudiante con este ID no existe: ');
       }
      let marks=JSON.parse(marksAsBytes.toString());
      
      return JSON.stringify(marks);
     }

   async agregaNotas(ctx,studentId,subject1,subject2,subject3) {
   
    let marks={
       subj1:subject1,
       subj2:subject2,
       subj3:subject3
       };

    await ctx.stub.putState(studentId,Buffer.from(JSON.stringify(marks))); 
    
    console.log('Notas de estudiante registrados correctamente');
    
  }

   async eliminaNotas(ctx,studentId) {
   

    await ctx.stub.deleteState(studentId); 
    
    console.log('Notas de estuante eliminados correctamente');
    
    }
}

module.exports=testContract;
